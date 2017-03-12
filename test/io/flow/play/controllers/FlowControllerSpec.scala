package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.MockConfig
import io.flow.play.util.{AuthData, Config, FlowSession, OrgAuthData}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import org.joda.time.DateTime
import org.scalatestplus.play._
import play.api.Application

class FlowControllerSpec extends PlaySpec with OneAppPerSuite {

  private[this] lazy val mockConfig = play.api.Play.current.injector.instanceOf[MockConfig]
  private[this] lazy val salt = "test"

  // TODO: Bind to the specific instance of mockConfig
  override lazy val app: Application = new GuiceApplicationBuilder().bindings(bind[Config].to[MockConfig]).build()

  private[this] val user = UserReference("usr-20151006-1")
  private[this] val session = FlowSession(id = "F51test")

  private[this] val controller = new FlowController {
    override def config: MockConfig = mockConfig
    override def jwtSalt: String = salt
  }

  "parse AuthData.AnonymousAuth w/ no user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None
    )

    controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ no user and session" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = Some(session)
    )

    controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session =  None
    )

    controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user and session" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session =  Some(session)
    )

    controller.parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.SessionAuth" in {
    val data = AuthData.Session(
      requestId = "test",
      session =  session
    )

    controller.parse(data.jwt(salt))(AuthData.Session.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse OrgAuthData.Identified" in {
    val data = OrgAuthData.Identified(
      requestId = "test",
      user = user,
      organization = "demo",
      environment = Environment.Sandbox,
      role = Role.Member,
      session = None
    )

    controller.parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    controller.parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse OrgAuthData.Identified w/ session" in {
    val data = OrgAuthData.Identified(
      requestId = "test",
      user = user,
      organization = "demo",
      environment = Environment.Sandbox,
      role = Role.Member,
      session = Some(session)
    )

    controller.parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse OrgAuthData.Session" in {
    val data = OrgAuthData.Session(
      requestId = "test",
      session = session,
      organization = "demo",
      environment = Environment.Sandbox
    )

    controller.parse(data.jwt(salt))(OrgAuthData.Session.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    controller.parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "expired" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None
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
