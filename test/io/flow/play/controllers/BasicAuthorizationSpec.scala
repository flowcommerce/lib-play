package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.play.controllers.Authorization.{Token, JWTToken}
import io.flow.play.util.EnvironmentConfig
import org.scalatest.{Matchers, FunSpec}


class BasicAuthorizationSpec extends FunSpec with Matchers {
  val jwtSalt = EnvironmentConfig.requiredString("JWT_SALT")

  def createJWTHeader(userId: String, salt: Option[String] = None): String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(Map("id" -> userId))
    val token = JsonWebToken(header, claimsSet, salt.getOrElse(jwtSalt))
    s"Bearer $token"
  }

  describe("BasicAuthorization") {
    describe("Basic") {
      it("should decode a basic auth header") {
        val headerValue = "Basic YWRtaW46"

        Authorization.get(headerValue).map { authToken =>
          authToken match {
            case Token(token) => token shouldBe "admin"
            case _ => fail("Did not parse a Token, got a different type instead.")
          }
        }.getOrElse(fail("Could not parse token!"))
      }
    }

    describe("JWT") {
      it("should decode") {
        val userId = "usr-20160130-1"
        val headerValue = createJWTHeader(userId = userId)

        Authorization.get(headerValue).map { authToken =>
          authToken match {
            case JWTToken(id) => id shouldBe userId
            case _ => fail("Did not parse a JWTToken, got a different type instead.")
          }
        }.getOrElse(fail("Could not parse token!"))
      }

      it("should fail to decode") {
        val userId = "usr-20160130-1"
        val headerValue = createJWTHeader(userId = userId, salt = Some("a different salt"))

        val authorization = Authorization.get(headerValue).map { authToken =>
          authToken match {
            case t:JWTToken => fail("expected not to get a token due to bad salt.")
            case _ => fail("Did not parse a JWTToken, got a different type instead.")
          }
        }

        authorization shouldEqual None
      }
    }
  }

}
