package io.flow.play.jwt

import io.flow.secret.internal.v0.models.{SecretConfig, Secret => JwtSecret}
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class JwtSecretsRetrieverServiceSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with Eventually {

  implicit private val ec: ExecutionContext = app.actorSystem.dispatcher

  private val secret1 = JwtSecret("1", "secret1", DateTime.now)
  private val secret2 = JwtSecret("2", "secret2", DateTime.now)
  private val secret3 = JwtSecret("3", "secret3", DateTime.now)

  private val defaultSecrets = Seq(secret1, secret2, secret3)
  private val defaultSecretsConfig = SecretConfig(
    secrets = defaultSecrets,
    preferredSecretId = secret2.id
  )

  "RefreshingJwtSecretsRetrieverService" should {

    "get simple secrets" in {
      val dao = mock[JwtSecretsDao]
      when(dao.get).thenReturn(Future.successful(defaultSecretsConfig))
      val service = new RefreshingJwtSecretsRetrieverService(app.actorSystem, dao, 1.hour)

      service.get mustBe JwtSecrets(all = defaultSecrets.map(s => s.id -> s).toMap, encoding = secret2)
    }

    "get and refreshes every reload interval" in {
      val dao = mock[JwtSecretsDao]
      when(dao.get).thenReturn(Future.successful(defaultSecretsConfig))

      val service = new RefreshingJwtSecretsRetrieverService(app.actorSystem, dao, 100.millis)

      service.get mustBe JwtSecrets(all = defaultSecrets.map(s => s.id -> s).toMap, encoding = secret2)

      // update result returned by dao
      val newSecrets =  defaultSecrets.take(2)
      when(dao.get).thenReturn(Future.successful(defaultSecretsConfig.copy(secrets = newSecrets)))

      eventually(Timeout(200.millis)) {
        service.get mustBe JwtSecrets(all = newSecrets.map(s => s.id -> s).toMap, encoding = secret2)
      }
    }

    "fail to init if dao fails to get" in {
      val dao = mock[JwtSecretsDao]
      when(dao.get).thenReturn(Future.failed(new IllegalStateException("boom")))

      a[IllegalStateException] mustBe thrownBy(new RefreshingJwtSecretsRetrieverService(app.actorSystem, dao, 1.hour))
    }

    "fall back to last successful get if dao fails and then recovers" in {
      // init
      val dao = mock[JwtSecretsDao]
      when(dao.get).thenReturn(Future.successful(defaultSecretsConfig))
      val service = new RefreshingJwtSecretsRetrieverService(app.actorSystem, dao, 100.millis)

      service.get mustBe JwtSecrets(all = defaultSecrets.map(s => s.id -> s).toMap, encoding = secret2)

      when(dao.get).thenReturn(Future.failed(new IllegalStateException("boom")))

      Thread.sleep(200)

      // unchanged
      service.get mustBe JwtSecrets(all = defaultSecrets.map(s => s.id -> s).toMap, encoding = secret2)

      // update result returned by dao
      val newSecrets =  defaultSecrets.take(2)
      when(dao.get).thenReturn(Future.successful(defaultSecretsConfig.copy(secrets = newSecrets)))

      eventually(Timeout(200.millis)) {
        service.get mustBe JwtSecrets(all = newSecrets.map(s => s.id -> s).toMap, encoding = secret2)
      }
    }

  }

}
