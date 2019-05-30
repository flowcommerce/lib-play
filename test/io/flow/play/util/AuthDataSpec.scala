package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.log.RollbarProvider
import io.flow.play.util.AuthDataMap.Fields

class AuthDataSpec extends LibPlaySpec {

  private[this] val logger = RollbarProvider.logger("test")

  "AuthData.user" in {
    val id = "user-1"
    val auth = AuthHeaders.user(UserReference(id), requestId = "test1")
    auth.requestId must be("test1")
    auth.user.id must be(id)
  }

  "AuthData.organization defaults" in {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo")
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

  "AuthData.generateRequestId" in {
    AuthHeaders.generateRequestId("foo").startsWith("foo") must be(true)

    val all = 1.to(100).map { _ => AuthHeaders.generateRequestId("foo") }
    all.distinct.size must be(100)
  }

  "OrgAuthData.Checkout - customer" in {
    val orgAuthDataCustomer = AuthHeaders.organizationCustomer(org = createTestId())
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
    val authDataSession = AuthHeaders.organizationSession(createTestId())
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

}
