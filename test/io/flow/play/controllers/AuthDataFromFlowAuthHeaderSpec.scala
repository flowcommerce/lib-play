package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.MockConfig
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import org.joda.time.DateTime
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

  "parse AuthData.AnonymousAuth w/ no user" in {
    val ts = new DateTime()
    val data = AuthData.AnonymousAuth(
      requestId = "test",
      createdAt = ts,
      user = None
    )

    val result = testTrait.parse(data.jwt(salt)).getOrElse {
      sys.error("Failed to parse")
    }.asInstanceOf[AuthData.AnonymousAuth]
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(None)
  }

  "parse AuthData.IdentifiedAuth" in {
    val ts = new DateTime()
    val user = UserReference("usr-20151006-1")
    val data = AuthData.IdentifiedAuth(
      requestId = "test",
      createdAt = ts,
      user = user
    )

    val result = testTrait.parse(data.jwt(salt)).getOrElse {
      sys.error("Failed to parse")
    }.asInstanceOf[AuthData.IdentifiedAuth]
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(user)
  }
  /*
    "parse w/ user and organization" in {
      val ts = new DateTime()
      val user = UserReference("usr-20151006-1")
      val org = OrganizationAuthData(
        organization = "flow-sandbox",
        role = Role.Member,
        environment = Environment.Sandbox
      )
      val data = AuthHeaders(
        requestId = "test2",
        createdAt = ts,
        user = user,
        organization = Some(org)
      )

      val result = testTrait.parse(data.jwt(salt)).getOrElse {
        sys.error("Failed to parse")
      }
      result.requestId must be("test2")
      result.createdAt must be(ts)
      result.user must be(user)
      result.organization must be(Some(org))
    }

    "expired" in {
      val ts = (new DateTime()).plusMinutes(5)
      val user = UserReference("usr-20151006-1")
      val data = AuthHeaders(
        requestId = "test2",
        createdAt = ts,
        user = user,
        organization = None
      )

      testTrait.parse(data.jwt(salt)) must be(None)
    }
    */
}
