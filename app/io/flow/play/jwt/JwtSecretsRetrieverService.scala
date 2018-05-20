package io.flow.play.jwt

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import io.flow.secret.internal.v0.models.{RedactedSecret, RedactedSecretConfig, SecretConfig, Secret => JwtSecret}
import io.flow.secret.internal.v0.models.json._
import javax.inject.{Inject, Named, Singleton}
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class JwtSecrets(all: Map[String, JwtSecret], encoding: JwtSecret)

trait JwtSecretsRetrieverService {

  def get: JwtSecrets

}

@Singleton
class RefreshingJwtSecretsRetrieverService @Inject() (
  system: ActorSystem,
  jwtSecretsDao: JwtSecretsDao,
  @Named("jwtSecretsReloadInterval") reloadInterval: FiniteDuration
)(implicit ec: ExecutionContext) extends JwtSecretsRetrieverService {

  import RefreshingJwtSecretsRetrieverService._

  // blocks on init
  private val secrets = {
    val secretsFuture = getAndTransformRetries(1)
    secretsFuture.onComplete {
      case Failure(ex) =>
        Logger.error(s"[JwtSecretsError] Error when initializing JWT secrets after $MaxAttempts attempts. Failed to initialize.", ex)
      case _ =>
    }
    val (secretsConfig, transformedSecrets) = Await.result(secretsFuture, 30.seconds)
    logNew(secretsConfig)
    new AtomicReference[JwtSecrets](transformedSecrets)
  }

  system.scheduler.schedule(reloadInterval, reloadInterval)(getAndUpdate())

  override def get: JwtSecrets = secrets.get()

  private def transformSecrets(jwtSecrets: SecretConfig): Try[JwtSecrets] = {
    val all = jwtSecrets.secrets.map(s => s.id -> s).toMap
    all
      .get(jwtSecrets.preferredSecretId)
      .map(e => Success(JwtSecrets(all, e)))
      .getOrElse(Failure(new IllegalStateException(s"Preferred id [${jwtSecrets.preferredSecretId}] could not be found in the list of provided secrets")))
  }

  private def getAndTransformRetries(attempts: Int): Future[(SecretConfig, JwtSecrets)] = {
    jwtSecretsDao.get.flatMap(s => Future.fromTry(transformSecrets(s).map(t => (s, t)))).recoverWith {
      case t: Throwable if attempts < MaxAttempts =>
        Logger.warn(s"[JwtSecretsWarn] Error when retrieving JWT secrets ($attempts/$MaxAttempts). Retrying...", t)
        getAndTransformRetries(attempts + 1)
    }
  }

  private def getAndUpdate(): Unit = {
    getAndTransformRetries(1).onComplete {
      case Success((newConfig, newTransformed)) =>
        setAndLogIfNew(newConfig, newTransformed)
      case Failure(ex) =>
        Logger.error(s"[JwtSecretsError] Error when retrieving JWT secrets after $MaxAttempts attempts. Jwt secrets not updating.", ex)
    }
  }

  private def setAndLogIfNew(newConfig: SecretConfig, newTransformed: JwtSecrets): Unit = {
    val old = secrets.getAndSet(newTransformed)
    if (old != newTransformed)
      logNew(newConfig)
  }

  private def logNew(newConfig: SecretConfig): Unit = {
    val redactedNew = RedactedSecretConfig(
      secrets = newConfig.secrets.map(s => RedactedSecret(id = s.id, createdAt = s.createdAt)),
      preferredSecretId = newConfig.preferredSecretId
    )
    Logger.info(s"[RefreshingJwtSecretsRetrieverService] updated secrets to ${Json.toJson(redactedNew).toString()}")
  }

}

object RefreshingJwtSecretsRetrieverService {
  private val MaxAttempts = 3
}

@Singleton
class MockJwtSecretsRetrieverService @Inject() () extends JwtSecretsRetrieverService {
  private val secret = JwtSecret("id", "mocksecret", createdAt = DateTime.now())
  override val get: JwtSecrets = JwtSecrets(all = Map("id" -> secret), encoding = secret)
}
