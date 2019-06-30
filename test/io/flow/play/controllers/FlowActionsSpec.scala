package io.flow.play.controllers

import com.typesafe.config.ConfigFactory
import io.flow.common.v0.models.{CustomerReference, Environment, Role, UserReference}
import io.flow.log.RollbarLogger
import io.flow.play.clients.MockConfig
import io.flow.play.util._
import org.joda.time.DateTime
import play.api.Configuration

class FlowActionsSpec extends LibPlaySpec with FlowActionInvokeBlockHelper {

  private[this] lazy val mockConfig = new MockConfig(new DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty()))))

  private[this] val user = UserReference("usr-20151006-1")
  private[this] val session = AuthHeaders.createFlowSession()
  private[this] val customer = CustomerReference(number = createTestId())

  private[this] implicit val logger: RollbarLogger = RollbarLogger.SimpleLogger

  override def config: MockConfig = mockConfig

  override def jwtSalt: String = "test"

  private[this] def parseAuthData[T <: AuthData](data: AuthData)(f: Map[String, String] => Option[T]) = {
    parse(data.jwt(jwtSalt))(f).getOrElse {
      sys.error("Failed to parse")
    }
  }

  private[this] def validateParse[T <: AuthData](data: AuthData)(f: Map[String, String] => Option[T]) = {
    parseAuthData(data)(f) must be(data)
  }

  "parse AuthData.AnonymousAuth w/ no user" in {
    validateParse(
      AuthData.Anonymous.Empty
    )(AuthData.Anonymous.fromMap)
  }

  "parse AuthData.AnonymousAuth w/ no user and session" in {
    validateParse(
      AuthData.Anonymous.Empty.copy(
        session = Some(session)
      )
    )(AuthData.Anonymous.fromMap)
  }

  "parse AuthData.AnonymousAuth w/ no user, no session and customer" in {
    validateParse(
      AuthData.Anonymous.Empty.copy(
        customer = Some(customer)
      )
    )(AuthData.Anonymous.fromMap)
  }

  "parse AuthData.AnonymousAuth w/ user" in {
    validateParse(
      AuthData.Anonymous.Empty.copy(
        user = Some(user)
      )
    )(AuthData.Anonymous.fromMap)
  }

  "parse AuthData.AnonymousAuth w/ user and session and customer" in {
    validateParse(
      AuthData.Anonymous.Empty.copy(
        user = Some(user),
        session = Some(session),
        customer = Some(customer)
      )
    )(AuthData.Anonymous.fromMap)
  }

  "parse AuthData.SessionAuth" in {
    validateParse(
      AuthData.Session(
        requestId = createTestId(),
        session = session
      )
    )(AuthData.Session.fromMap)
  }

  "parse AuthData.Customer" in {
    validateParse(
      AuthData.Customer(
        requestId = createTestId(),
        session = session,
        customer = customer
      )
    )(AuthData.Customer.fromMap)
  }

  "parse OrgAuthData.Identified" in {
    val auth = OrgAuthData.Identified(
      requestId = createTestId(),
      user = user,
      organization = createTestId(),
      environment = Environment.Sandbox,
      role = Role.Member,
      session = None,
      customer = None
    )
    validateParse(auth)(OrgAuthData.Identified.fromMap)
    validateParse(auth)(OrgAuthData.Org.fromMap)
  }

  "parse OrgAuthData.Identified w/ session" in {
    validateParse(
      OrgAuthData.Identified(
        requestId = createTestId(),
        user = user,
        organization = createTestId(),
        environment = Environment.Sandbox,
        role = Role.Member,
        session = Some(session),
        customer = None
      )
    )(OrgAuthData.Identified.fromMap)
  }

  "parse OrgAuthData.Identified w/ customer" in {
    validateParse(
      OrgAuthData.Identified(
        requestId = createTestId(),
        user = user,
        organization = createTestId(),
        environment = Environment.Sandbox,
        role = Role.Member,
        session = None,
        customer = Some(customer)
      )
    )(OrgAuthData.Identified.fromMap)
  }

  "parse OrgAuthData.Session" in {
    val auth = OrgAuthData.Session(
      requestId = createTestId(),
      session = session,
      organization = createTestId(),
      environment = Environment.Sandbox
    )
    validateParse(auth)(OrgAuthData.Session.fromMap)
    validateParse(auth)(OrgAuthData.Org.fromMap)
  }

  "parse OrgAuthData.Customer" in {
    validateParse(
      OrgAuthData.Customer(
        requestId = createTestId(),
        session = session,
        organization = createTestId(),
        environment = Environment.Sandbox,
        customer = customer
      )
    )(OrgAuthData.Customer.fromMap)
  }

  "expired" in {
    val data = AuthData.Anonymous.Empty
    validateParse(data)(AuthData.Anonymous.fromMap)
    validateParse(
      data.copy(createdAt = DateTime.now.minusMinutes(1))
    )(AuthData.Anonymous.fromMap)

    parse(data.copy(
      createdAt = DateTime.now.minusMinutes(5)
    ).jwt(jwtSalt))(AuthData.Anonymous.fromMap).isDefined must be(false)
  }
}
