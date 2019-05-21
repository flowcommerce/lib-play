package io.flow.play.controllers

import com.typesafe.config.ConfigFactory
import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.log.RollbarLogger
import io.flow.play.clients.MockConfig
import io.flow.play.util._
import org.joda.time.DateTime
import play.api.Configuration

class FlowActionsSpec extends LibPlaySpec with FlowActionInvokeBlockHelper {

  private[this] lazy val mockConfig = new MockConfig(new DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty()))))
  private[this] lazy val salt = "test"

  private[this] val user = UserReference("usr-20151006-1")
  private[this] val session = FlowSession(id = "F51test")
  private[this] val customer = CustomerReference(number = "tech@flow.io")

  implicit val logger = RollbarLogger.SimpleLogger

  override def config: MockConfig = mockConfig

  override def jwtSalt: String = salt

  "parse AuthData.AnonymousAuth w/ no user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None,
      customer = None
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ no user and session" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = Some(session),
      customer = None
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ no user, no session and customer" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None,
      customer = Some(customer)
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session = None,
      customer = None
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.AnonymousAuth w/ user and session and customer" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = Some(user),
      session = Some(session),
      customer = Some(customer)
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse AuthData.SessionAuth" in {
    val data = AuthData.Session(
      requestId = "test",
      session = session
    )

    parse(data.jwt(salt))(AuthData.Session.fromMap).getOrElse {
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
      session = None,
      customer = None
    )

    parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
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
      session = Some(session),
      customer = None
    )

    parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse OrgAuthData.Identified w/ customer" in {
    val data = OrgAuthData.Identified(
      requestId = "test",
      user = user,
      organization = "demo",
      environment = Environment.Sandbox,
      role = Role.Member,
      session = None,
      customer = Some(customer)
    )

    parse(data.jwt(salt))(OrgAuthData.Identified.fromMap).getOrElse {
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

    parse(data.jwt(salt))(OrgAuthData.Session.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)

    // Confirm generic org parser works
    parse(data.jwt(salt))(OrgAuthData.Org.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "parse OrgAuthData.Customer" in {
    val data = OrgAuthData.Customer(
      requestId = "test",
      session = session,
      organization = "demo",
      environment = Environment.Sandbox,
      customer = customer
    )

    parse(data.jwt(salt))(OrgAuthData.Customer.fromMap).getOrElse {
      sys.error("Failed to parse")
    } must be(data)
  }

  "expired" in {
    val data = AuthData.Anonymous(
      requestId = "test",
      user = None,
      session = None,
      customer = None
    )

    parse(data.jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(true)

    parse(data.copy(
      createdAt = DateTime.now.minusMinutes(1)
    ).jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(true)

    parse(data.copy(
      createdAt = DateTime.now.minusMinutes(5)
    ).jwt(salt))(AuthData.Anonymous.fromMap).isDefined must be(false)

  }
}
