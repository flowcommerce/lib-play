package io.flow.play.controllers

import io.flow.play.controllers.BasicAuthorization.{Token, JWTToken}
import org.scalatest.{Matchers, FunSpec}


class BasicAuthorizationSpec extends FunSpec with Matchers {
  describe("BasicAuthorization") {
    describe("Basic") {
      it("should decode a basic auth header") {
        val headerValue = "Basic YWRtaW46"

        BasicAuthorization.get(headerValue).map { authToken =>
          authToken match {
            case Token(token) => token shouldBe "admin"
            case _ => fail("Did not parse a Token, got a different type instead.")
          }
        }.getOrElse(fail("Could not parse token!"))
      }
    }

    describe("JWT") {
      it("should decode a basic auth bearer header") {
        val headerValue = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6InVzci0yMDE2MDEzMC0xIiwiaWF0IjoxNDU3NjIyMzg2LCJleHAiOjE0NTc3MDg3ODYsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3QiLCJzdWIiOiJ0ZXN0dXNlckB0ZXN0LmNvbSJ9.v9HkqkKEaiq88T6QjLkpiKW2mjM_mqqg2owW_SzxjCw"

        BasicAuthorization.get(headerValue).map { authToken =>
          authToken match {
            case JWTToken(userId) => userId shouldBe "usr-20160130-1"
            case _ => fail("Did not parse a JWTToken, got a different type instead.")
          }
        }.getOrElse(fail("Could not parse token!"))
      }
    }
  }

}
