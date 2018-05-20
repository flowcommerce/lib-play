package io.flow.play.jwt

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.secret.internal.v0.models.{Secret => JwtSecret}
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder


class JwtServiceSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with BeforeAndAfterEach with OptionValues with TryValues {

  private val jwtRetriever = mock[JwtSecretsRetrieverService]

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .overrides(bind[JwtSecretsRetrieverService].toInstance(jwtRetriever))
      .build()
  }

  private val fallbackSecret = JwtSecret("sec-201610-1", "secret1", DateTime.now)
  private val secret2 = JwtSecret("2", "secret2", DateTime.now)
  private val secret3 = JwtSecret("3", "secret3", DateTime.now)
  private val defaultSecrets = JwtSecrets(
    all = Seq(fallbackSecret, secret2, secret3).map(s => s.id -> s).toMap,
    encoding = secret2
  )

  override def beforeEach(): Unit = {
    when(jwtRetriever.get).thenReturn(defaultSecrets)
  }

  private val jwtService = app.injector.instanceOf[DefaultJwtService]

  "DefaultJwtService" should {

    "encode with correct secret" in {
      val token = jwtService.encode(Map("a" -> "b"))

      JsonWebToken.validate(token, secret2.key) mustBe true
      JsonWebToken.validate(token, "not secret 2") mustBe false
    }

    "add key id when encoding" in {
      val claims = Map("a" -> "b")
      val token = jwtService.encode(claims)

      val decodedClaims = JsonWebToken.unapply(token).value._2.asSimpleMap.success.value
      decodedClaims mustBe claims + ("kid" -> secret2.id)
    }

    "decode using the key id secret" in {
      val claims = Map("a" -> "b")
      val encoded = jwtService.encode(Map("a" -> "b"))

      val decoded = jwtService.decode(encoded)

      // ensure key id is "2"
      val encodedClaims = JsonWebToken.unapply(encoded).value._2.asSimpleMap.success.value
      encodedClaims mustBe claims + ("kid" -> secret2.id)

      // ensure decoded claims - note that it does not contain the key id
      decoded.success.value mustBe claims
    }

    "decode using the key id secret when the key id is not the preferred one" in {
      val claims = Map("a" -> "b")
      val encoded = jwtService.encode(Map("a" -> "b"))

      when(jwtRetriever.get).thenReturn(defaultSecrets.copy(encoding = secret3))

      val decoded = jwtService.decode(encoded)

      // ensure key id is "2"
      val encodedClaims = JsonWebToken.unapply(encoded).value._2.asSimpleMap.success.value
      encodedClaims mustBe claims + ("kid" -> secret2.id)

      // ensure decoded claims - note that it does not contain the key id
      decoded.success.value mustBe claims

      // ensure now preferred key id is "3"
      val encodedUpdated = jwtService.encode(Map("a" -> "b"))
      JsonWebToken.validate(encodedUpdated, secret3.key) mustBe true
    }

    "decode when key id is not present using fallback key id and fallback key is present in the list of secrets" in {
      val claims = Map("a" -> "b")
      val encoded = JsonWebToken.apply(JwtHeader("HS256"), JwtClaimsSet(claims), fallbackSecret.key)

      val decoded = jwtService.decode(encoded)

      decoded.success.value mustBe claims
    }

    "fail to decode if key id is not present and fallback key is not present in the list of secrets" in {
      val claims = Map("a" -> "b")
      val encoded = JsonWebToken.apply(JwtHeader("HS256"), JwtClaimsSet(claims), fallbackSecret.key)

      when(jwtRetriever.get).thenReturn(defaultSecrets.copy(all = defaultSecrets.all.filterKeys(_ != fallbackSecret.id)))

      val decoded = jwtService.decode(encoded)

      decoded.isFailure mustBe true
    }

    "fail to decode if key id is not present in the list of secrets" in {
      val claims = Map("a" -> "b")
      val encoded = JsonWebToken.apply(JwtHeader("HS256"), JwtClaimsSet(claims), "some key")

      val decoded = jwtService.decode(encoded)

      decoded.isFailure mustBe true
    }

  }

}
