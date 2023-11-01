package io.flow.play.controllers

import com.typesafe.config.ConfigFactory
import io.flow.log.RollbarProvider
import io.flow.play.clients.MockConfig
import io.flow.play.util.{ApplicationConfig, DefaultConfig, LibPlaySpec}
import pdi.jwt.JwtAlgorithm.HS256
import pdi.jwt.JwtJson
import play.api.Configuration
import play.api.libs.json.Json

class AuthorizationSpec extends LibPlaySpec {

  private[this] val logger = RollbarProvider.logger("test")

  private[this] lazy val mockConfig = new MockConfig(
    new DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty())))
  )

  def createJWTHeader(
    userId: String,
    salt: String = mockConfig.requiredString("JWT_SALT")
  ): String = {
    val token = JwtJson.encode(Json.obj("id" -> userId), salt, HS256)
    s"Bearer $token"
  }

  "Authorization" must {
    "Basic should decode a basic auth header" in {
      val headerValue = "Basic YWRtaW46"

      new AuthorizationImpl(logger, mockConfig)
        .get(headerValue)
        .map {
          case Token(token) => token must be("admin")
          case _ => fail("Did not parse a Token, got a different type instead.")
        }
        .getOrElse(fail("Could not parse token!"))
    }

    "Jwt should decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId)

      new AuthorizationImpl(logger, mockConfig)
        .get(headerValue)
        .map {
          case JwtToken(id) => id must be(userId)
          case _ => fail("Did not parse a JwtToken, got a different type instead.")
        }
        .getOrElse(fail("Could not parse token!"))
    }

    "Jwt should fail to decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId, salt = "a different salt")

      new AuthorizationImpl(logger, mockConfig).get(headerValue) match {
        case None => {
          // all good
        }
        case Some(authToken) => {
          authToken match {
            case _: JwtToken => fail("expected not to get a token due to bad salt.")
            case _ => fail("Did not parse a JwtToken, got a different type instead.")
          }
        }
      }
    }
  }

}
