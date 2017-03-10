package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.MockConfig
import io.flow.play.util.OrgAuthData.Org
import io.flow.play.util.{AuthData, Config, FlowSession, OrgAuthData}
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
  private[this] val session = FlowSession(id = "F51test")

  private[this] val controller = new FlowController {
    override def config = mockConfig
    override def jwtSalt = salt
  }

  "parse AuthData.AnonymousAuth w/ no user" in {
    val ts = DateTime.now
    val data = AuthData.Anonymous(
      requestId = "test",
      createdAt = ts,
      user = None
    )

    val result = controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    }
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(None)
  }

  "parse AuthData.AnonymousAuth w/ no user and session" in {
    val ts = DateTime.now
    val data = AuthData.Anonymous(
      requestId = "test",
      createdAt = ts,
      user = None
    )

    val result = controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    }
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(None)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    val ts = DateTime.now
    val data = AuthData.Anonymous(
      requestId = "test",
      createdAt = ts,
      user = Some(user)
    )

    val result = controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    }
    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(Some(user))
  }

  "parse OrgAuthData.Identified" in {
    val ts = DateTime.now
    val data = OrgAuthData.Identified(
      requestId = "test",
      createdAt = ts,
      user = user,
      organization = "demo",
      environment = Environment.Sandbox,
      role = Role.Member
    )

    val result = controller.parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    }

    result.requestId must be("test")
    result.createdAt must be(ts)
    result.user must be(user)
    result.organization must be("demo")
    result.environment must be(Environment.Sandbox)
    result.role must be(Role.Member)

    // Confirm generic org parser works
    controller.parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(result)
  }

  "parse OrgAuthData.Session" in {
    val ts = DateTime.now
    val data = OrgAuthData.Session(
      requestId = "test",
      session = session,
      createdAt = ts,
      organization = "demo",
      environment = Environment.Sandbox
    )

    val result = controller.parse(data.jwt(salt))(OrgAuthData.Session.fromMap).getOrElse {
      sys.error("Failed to parse")
    }

    result.requestId must be("test")
    result.createdAt must be(ts)
    result.session must be(session)
    result.organization must be("demo")
    result.environment must be(Environment.Sandbox)

    // Confirm generic org parser works
    controller.parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(result)
  }

  "expired" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      createdAt = DateTime.now,
      user = None
    )

    controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(true)

    controller.parse(data.copy(
      createdAt = DateTime.now.minusMinutes(1)
    ).jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(true)

    controller.parse(data.copy(
      createdAt = DateTime.now.minusMinutes(5)
    ).jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(false)

  }
}
