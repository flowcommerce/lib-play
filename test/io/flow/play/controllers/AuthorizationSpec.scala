package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.typesafe.config.ConfigFactory
import io.flow.play.clients.MockConfig
import io.flow.play.util.{ApplicationConfig, DefaultConfig}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import org.scalatestplus.play._

class AuthorizationSpec extends PlaySpec with GuiceOneAppPerSuite {

  private[this] lazy val mockConfig = MockConfig(DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty()))))

  def createJWTHeader(
    userId: String,
    salt: String = mockConfig.requiredString("JWT_SALT")
  ): String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(Map("id" -> userId))
    val token = JsonWebToken(header, claimsSet, salt)
    s"Bearer $token"
  }

  "Authorization" must {
    "Basic should decode a basic auth header" in {
      val headerValue = "Basic YWRtaW46"

      new AuthorizationImpl(mockConfig).get(headerValue).map { authToken =>
        authToken match {
          case Token(token) => token must be("admin")
          case _ => fail("Did not parse a Token, got a different type instead.")
        }
      }.getOrElse(fail("Could not parse token!"))
    }

    "Jwt should decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId)

      new AuthorizationImpl(mockConfig).get(headerValue).map { authToken =>
        authToken match {
          case JwtToken(id) => id must be(userId)
          case _ => fail("Did not parse a JwtToken, got a different type instead.")
        }
      }.getOrElse(fail("Could not parse token!"))
    }

    "Jwt should fail to decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId, salt = "a different salt")

      new AuthorizationImpl(mockConfig).get(headerValue) match {
        case None => {
          // all good
        }
        case Some(authToken) => {
          authToken match {
            case t:JwtToken => fail("expected not to get a token due to bad salt.")
            case _ => fail("Did not parse a JwtToken, got a different type instead.")
          }
        }
      }
    }
  }

}
