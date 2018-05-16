package io.flow.play.jwt

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import io.flow.secret.internal.v0.models.{SecretConfig, Secret => JwtSecret}
import javax.inject.{Inject, Named, Singleton}
import org.joda.time.DateTime
import play.api.Logger

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
    new AtomicReference[JwtSecrets](Await.result(secretsFuture, 30.seconds))
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

  private def getAndTransformRetries(attempts: Int): Future[JwtSecrets] = {
    jwtSecretsDao.get.flatMap(s => Future.fromTry(transformSecrets(s))).recoverWith {
      case t: Throwable if attempts < MaxAttempts =>
        Logger.warn(s"[JwtSecretsWarn] Error when retrieving JWT secrets ($attempts/$MaxAttempts). Retrying...", t)
        getAndTransformRetries(attempts + 1)
    }
  }

  private def getAndUpdate(): Unit = {
    getAndTransformRetries(1).onComplete {
      case Success(res) => secrets.set(res)
      case Failure(ex) =>
        Logger.error(s"[JwtSecretsError] Error when retrieving JWT secrets after $MaxAttempts attempts. Jwt secrets not updating.", ex)
    }
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
