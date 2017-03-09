package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.MockConfig
import io.flow.play.util.OrgData.{AnonymousOrgData, IdentifiedOrgData}
import io.flow.play.util.{AuthData, Config}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import org.joda.time.DateTime
import org.scalatestplus.play._

class FlowControllerSpec extends PlaySpec with OneAppPerSuite {

  private[this] lazy val mockConfig = play.api.Play.current.injector.instanceOf[MockConfig]
  private[this] lazy val salt = "test"

  // TODO: Bind to the specific instance of mockConfig
  override lazy val app = new GuiceApplicationBuilder().bindings(bind[Config].to[MockConfig]).build()

  private[this] val user = UserReference("usr-20151006-1")

  private[this] val controller = new FlowController {
    override def config = mockConfig
    override def jwtSalt = salt
  }

  "parse AuthData.AnonymousAuth w/ no user" in {
    val ts = DateTime.now
    val data = AuthData.AnonymousAuth(
      requestId = "test",
      createdAt = ts,
      user = None
    )

    val result = controller.parse(data.jwt(salt))(AuthData.AnonymousAuth.fromMap).getOrElse {
      sys.error("Failed to parse")
    }
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(None)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    val ts = DateTime.now
    val data = AuthData.AnonymousAuth(
      requestId = "test",
      createdAt = ts,
      user = Some(user)
    )

    val result = controller.parse(data.jwt(salt))(AuthData.AnonymousAuth.fromMap).getOrElse {
      sys.error("Failed to parse")
    }
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(Some(user))
  }

  "parse AuthData.IdentifiedOrgAuth" in {
    val ts = DateTime.now
    val data = AuthData.IdentifiedOrgAuth(
      requestId = "test",
      createdAt = ts,
      user = user,
      orgData = IdentifiedOrgData(
        organization = "demo",
        environment = Environment.Sandbox,
        role = Role.Member
      )
    )

    val result = controller.parse(data.jwt(salt))(AuthData.IdentifiedOrgAuth.fromMap).getOrElse {
      sys.error("Failed to parse")
    }

    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(user)
    result.orgData.organization must be("demo")
    result.orgData.environment must be(Environment.Sandbox)
    result.orgData.role must be(Role.Member)
  }

  "parse AuthData.AnonymousOrgAuth" in {
    val ts = DateTime.now
    val data = AuthData.AnonymousOrgAuth(
      requestId = "test",
      createdAt = ts,
      user = Some(user),
      orgData = AnonymousOrgData(
        organization = "demo",
        environment = Environment.Sandbox
      )
    )

    val result = controller.parse(data.jwt(salt))(AuthData.AnonymousOrgAuth.fromMap).getOrElse {
      sys.error("Failed to parse")
    }

    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(Some(user))
    result.orgData.organization must be("demo")
    result.orgData.environment must be(Environment.Sandbox)
  }

  "expired" in {
    val data = AuthData.AnonymousAuth(
      requestId = "test",
      createdAt = DateTime.now,
      user = None
    )

    controller.parse(data.jwt(salt))(AuthData.AnonymousOrgAuth.fromMap).isDefined must be(true)

    controller.parse(data.copy(
      createdAt = DateTime.now.minusMinutes(1)
    ).jwt(salt))(AuthData.AnonymousOrgAuth.fromMap).isDefined must be(true)

    controller.parse(data.copy(
      createdAt = DateTime.now.minusMinutes(5)
    ).jwt(salt))(AuthData.AnonymousOrgAuth.fromMap).isDefined must be(false)

  }
}
