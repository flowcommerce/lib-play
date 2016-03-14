package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.play.clients.MockConfig
import io.flow.play.controllers.Authorization.{Token, JwtToken}
import io.flow.play.util.Config

import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._

import org.scalatest._
import org.scalatestplus.play._

class AuthorizationSpec extends PlaySpec with OneAppPerSuite {

  private[this] lazy val mockConfig = play.api.Play.current.injector.instanceOf[MockConfig]

  // TODO: Bind to the specific instance of mockConfig
  override lazy val app = new GuiceApplicationBuilder().bindings(bind[Config].to[MockConfig]).build()

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

      Authorization.get(headerValue).map { authToken =>
        authToken match {
          case Token(token) => token must be("admin")
          case _ => fail("Did not parse a Token, got a different type instead.")
        }
      }.getOrElse(fail("Could not parse token!"))
    }

    "Jwt should decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId)

      Authorization.get(headerValue).map { authToken =>
        authToken match {
          case JwtToken(id) => id must be(userId)
          case _ => fail("Did not parse a JwtToken, got a different type instead.")
        }
      }.getOrElse(fail("Could not parse token!"))
    }

    "Jwt should fail to decode" in {
      val userId = "usr-20160130-1"
      val headerValue = createJWTHeader(userId = userId, salt = "a different salt")

      Authorization.get(headerValue) match {
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
