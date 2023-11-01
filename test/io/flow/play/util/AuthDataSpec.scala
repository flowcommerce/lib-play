package io.flow.play.util

import io.flow.common.v0.models.{CustomerReference, Environment, Role, UserReference}
import io.flow.log.RollbarProvider
import io.flow.play.util.AuthDataMap.Fields
import org.joda.time.DateTime

class AuthDataSpec extends LibPlaySpec {

  private[this] val logger = RollbarProvider.logger("test")

  "AuthData.user" in {
    val id = "user-1"
    val auth = AuthHeaders.user(UserReference(id), requestId = "test1")
    auth.requestId must be("test1")
    auth.user.id must be(id)
  }

  "AuthData.organization defaults" in {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo", environment = Environment.Sandbox)
    auth.user.id must be("user-1")
    auth.organization must be("demo")
    auth.environment must be(Environment.Sandbox)
    auth.role must be(Role.Member)
  }

  "AuthData.organization" in {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo", Role.Admin, Environment.Production)
    auth.user.id must be("user-1")
    auth.organization must be("demo")
    auth.environment must be(Environment.Production)
    auth.role must be(Role.Admin)
  }

  "AuthData.channel defaults" in {
    val auth = AuthHeaders.channel(UserReference("user-1"), "testify")
    auth.user.id must be("user-1")
    auth.channel must be("testify")
  }

  "AuthData.channel" in {
    val requestId = "test-request"
    val sessionId = "F51session"
    val customer = "customerRef"
    val auth = AuthHeaders.channel(
      UserReference("user-1"),
      "testify",
      requestId = requestId,
      session = Some(FlowSession(sessionId)),
      customer = Some(CustomerReference(customer))
    )
    auth.user.id must be("user-1")
    auth.channel must be("testify")
    auth.requestId must be(requestId)
    auth.session must be(Some(FlowSession(sessionId)))
    auth.customer must be(Some(CustomerReference(customer)))
  }

  "AuthData.generateRequestId" in {
    AuthHeaders.generateRequestId("foo").startsWith("foo") must be(true)

    val all = 1.to(100).map { _ => AuthHeaders.generateRequestId("foo") }
    all.distinct.size must be(100)
  }

  "OrgAuthData.Checkout - customer" in {
    val orgAuthDataCustomer = AuthHeaders.organizationCustomer(org = createTestId(), environment = Environment.Sandbox)
    val customerData: Map[String, String] = Map(
      Fields.CreatedAt -> orgAuthDataCustomer.createdAt.toString,
      Fields.RequestId -> orgAuthDataCustomer.requestId,
      Fields.Organization -> orgAuthDataCustomer.organization,
      Fields.Environment -> orgAuthDataCustomer.environment.toString,
      Fields.Session -> orgAuthDataCustomer.session.id,
      Fields.Customer -> orgAuthDataCustomer.customer.number
    )

    val auth: Option[AuthData] = OrgAuthData.Checkout.fromMap(customerData)(logger)

    auth.get.isInstanceOf[OrgAuthData.Customer] must be(true)
    val customer = auth.get.asInstanceOf[OrgAuthData.Customer]
    customer must be(orgAuthDataCustomer)

    val orgAuth: Option[AuthData] = OrgAuthData.CheckoutOrg.fromMap(customerData)(logger)

    orgAuth.get.isInstanceOf[OrgAuthData.Customer] must be(true)
    val customer2 = auth.get.asInstanceOf[OrgAuthData.Customer]
    customer2 must be(orgAuthDataCustomer)
  }

  "OrgAuthData.Checkout - session" in {
    val authDataSession = AuthHeaders.session()
    val sessionData: Map[String, String] = Map(
      Fields.CreatedAt -> authDataSession.createdAt.toString,
      Fields.RequestId -> authDataSession.requestId,
      Fields.Session -> authDataSession.session.id
    )

    val auth: Option[AuthData] = OrgAuthData.Checkout.fromMap(sessionData)(logger)

    auth.get.isInstanceOf[AuthData.Session] must be(true)
    val session = auth.get.asInstanceOf[AuthData.Session]
    session must be(authDataSession)
  }

  "OrgAuthData.CheckoutOrg - session org" in {
    val authDataSession = AuthHeaders.organizationSession(createTestId(), environment = Environment.Sandbox)
    val sessionData: Map[String, String] = Map(
      Fields.CreatedAt -> authDataSession.createdAt.toString,
      Fields.RequestId -> authDataSession.requestId,
      Fields.Session -> authDataSession.session.id,
      Fields.Organization -> authDataSession.organization,
      Fields.Environment -> authDataSession.environment.toString
    )

    val auth: Option[AuthData] = OrgAuthData.CheckoutOrg.fromMap(sessionData)(logger)

    auth.get.isInstanceOf[OrgAuthData.Session] must be(true)
    val session = auth.get.asInstanceOf[OrgAuthData.Session]
    session must be(authDataSession)
  }

  "OrgAuthData.IdentifiedCustomer - customer" in {
    val orgAuthDataCustomer = AuthHeaders.organizationCustomer(org = createTestId(), environment = Environment.Sandbox)
    val customerData: Map[String, String] = Map(
      Fields.CreatedAt -> orgAuthDataCustomer.createdAt.toString,
      Fields.RequestId -> orgAuthDataCustomer.requestId,
      Fields.Organization -> orgAuthDataCustomer.organization,
      Fields.Environment -> orgAuthDataCustomer.environment.toString,
      Fields.Session -> orgAuthDataCustomer.session.id,
      Fields.Customer -> orgAuthDataCustomer.customer.number
    )

    val auth: Option[AuthData] = OrgAuthData.IdentifiedCustomer.fromMap(customerData)(logger)

    auth.get.isInstanceOf[OrgAuthData.Customer] must be(true)
    val customer = auth.get.asInstanceOf[OrgAuthData.Customer]
    customer must be(orgAuthDataCustomer)
  }

  "OrgAuthData.Anonymous with org data" in {
    val orgAuthDataCustomer = AuthHeaders.organizationCustomer(org = createTestId(), environment = Environment.Sandbox)
    val customerData: Map[String, String] = Map(
      Fields.CreatedAt -> orgAuthDataCustomer.createdAt.toString,
      Fields.RequestId -> orgAuthDataCustomer.requestId,
      Fields.Organization -> orgAuthDataCustomer.organization,
      Fields.Session -> orgAuthDataCustomer.session.id,
      Fields.Customer -> orgAuthDataCustomer.customer.number
    )

    val auth = AuthData.Anonymous.fromMap(customerData)(logger).get
    auth.customer must equal(Some(orgAuthDataCustomer.customer))
    auth.session must equal(Some(orgAuthDataCustomer.session))
    auth.organization must equal(Some(orgAuthDataCustomer.organization))

    val anonAuthData = AuthData.Anonymous.Empty.copy(createdAt = DateTime.now)
    val anonData = Map(
      Fields.CreatedAt -> anonAuthData.createdAt.toString,
      Fields.RequestId -> anonAuthData.requestId
    )

    val anonAuth = AuthData.Anonymous.fromMap(anonData)(logger).get
    anonAuth.customer must be(empty)
    anonAuth.session must be(empty)
    anonAuth.organization must be(empty)
  }

}
