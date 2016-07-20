package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.MockConfig
import io.flow.play.controllers.Authorization.{Token, JwtToken}
import io.flow.play.util.{AuthData, OrganizationAuthData, Config}

import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._

import org.joda.time.DateTime
import org.scalatest._
import org.scalatestplus.play._

class AuthDataFromFlowAuthHeaderSpec extends PlaySpec with OneAppPerSuite {

  private[this] lazy val mockConfig = play.api.Play.current.injector.instanceOf[MockConfig]
  private[this] lazy val salt = "test"

  // TODO: Bind to the specific instance of mockConfig
  override lazy val app = new GuiceApplicationBuilder().bindings(bind[Config].to[MockConfig]).build()

  private[this] val testTrait = new AuthDataFromFlowAuthHeader {
    override def tokenClient = sys.error("Not supported")
    override def config = mockConfig
    override def jwtSalt = salt
  }

  "parse w/ user only" in {
    val ts = new DateTime()
    val user = UserReference("usr-20151006-1")
    val data = AuthData(
      createdAt = ts,
      user = user,
      organization = None
    )

    val result = testTrait.parse(data.jwt(salt)).getOrElse {
      sys.error("Failed to parse")
    }
    result.createdAt must be(ts)
    result.user must be(user)
    result.organization must be(None)
  }

  "parse w/ user and organization" in {
    val ts = new DateTime()
    val user = UserReference("usr-20151006-1")
    val org = OrganizationAuthData(
      organization = "flow-sandbox",
      role = Role.Member,
      environment = Environment.Sandbox
    )
    val data = AuthData(
      createdAt = ts,
      user = user,
      organization = Some(org)
    )

    val result = testTrait.parse(data.jwt(salt)).getOrElse {
      sys.error("Failed to parse")
    }
    result.createdAt must be(ts)
    result.user must be(user)
    result.organization must be(Some(org))
  }
  
  "expired" in {
    val ts = (new DateTime()).plusMinutes(5)
    val user = UserReference("usr-20151006-1")
    val data = AuthData(
      createdAt = ts,
      user = user,
      organization = None
    )

    testTrait.parse(data.jwt(salt)) must be(None)
  }
}
