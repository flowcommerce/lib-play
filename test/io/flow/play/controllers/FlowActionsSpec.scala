package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.clients.{JwtModule, MockConfig}
import io.flow.play.jwt.JwtService
import io.flow.play.util._
import org.joda.time.DateTime
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class FlowActionsSpec extends LibPlaySpec with FlowActionInvokeBlockHelper {

  override def fakeApplication(): Application = {
    val builder = new GuiceApplicationBuilder()
    builder.overrides(new JwtModule(builder.environment, builder.configuration)).build()
  }

  private[this] val user = UserReference("usr-20151006-1")
  private[this] val session = FlowSession(id = "F51test")

  override def config: MockConfig = app.injector.instanceOf[MockConfig]

  override def jwtService: JwtService = app.injector.instanceOf[JwtService]

  "parse AuthData.AnonymousAuth w/ no user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ no user and session" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = Some(session)
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session = None
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user and session" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session = Some(session)
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.SessionAuth" in {
    val data = AuthData.Session(
      requestId = "test",
      session = session
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Session.fromMap).getOrElse {
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

    parse(jwtService.encode(data.toClaims))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    parse(jwtService.encode(data.toClaims))(OrgAuthData.Org.fromMap).getOrElse {
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

    parse(jwtService.encode(data.toClaims))(OrgAuthData.Identified.fromMap).getOrElse {
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

    parse(jwtService.encode(data.toClaims))(OrgAuthData.Session.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    parse(jwtService.encode(data.toClaims))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "expired" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None
    )

    parse(jwtService.encode(data.toClaims))(AuthData.Anonymous.fromMap).isDefined must be(true)

    parse(
      jwtService.encode(data.copy(createdAt = DateTime.now.minusMinutes(1)).toClaims)
    )(AuthData.Anonymous.fromMap).isDefined must be(true)

    parse(
      jwtService.encode(data.copy(createdAt = DateTime.now.minusMinutes(5)).toClaims)
    )(AuthData.Anonymous.fromMap).isDefined must be(false)

  }
}
