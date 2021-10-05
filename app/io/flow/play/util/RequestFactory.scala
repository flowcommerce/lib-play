package io.flow.play.util

import javax.inject.{Inject, Singleton}
import play.api.mvc.Request

import io.flow.log.RollbarLogger
import io.flow.play.controllers._

@Singleton
class RequestFactory @Inject()(myConfig: Config)(implicit logger: RollbarLogger) {

    private val helper = new FlowActionInvokeBlockHelper { val config = myConfig }
    private def build[A, B <: AuthData](
        request: Request[A],
        authDataFromMap: Map[String, String] => Option[B],
    ): Option[B] = helper.auth(request.headers)(authDataFromMap(_))

    def anonymous[A](request: Request[A]): AnonymousRequest[A] = {
        val auth = build(request, AuthData.Anonymous.fromMap)
            .getOrElse(AuthData.Anonymous.Empty) // Create an empty header here so at least requestId tracking can start

        new AnonymousRequest(auth, request)
    }

    def checkoutOrg[A](request: Request[A]): Option[CheckoutOrgRequest[A]] = build(request, OrgAuthData.CheckoutOrg.fromMap).map(new CheckoutOrgRequest(_, request))
    def checkout[A](request: Request[A]): Option[CheckoutRequest[A]] = build(request, OrgAuthData.Checkout.fromMap).map(new CheckoutRequest(_, request))
    def customerOrg[A](request: Request[A]): Option[CustomerOrgRequest[A]] = build(request, OrgAuthData.Customer.fromMap).map(new CustomerOrgRequest(_, request))
    def customer[A](request: Request[A]): Option[CustomerRequest[A]] = build(request, AuthData.Customer.fromMap).map(new CustomerRequest(_, request))
    def identifiedCustomer[A](request: Request[A]): Option[IdentifiedCustomerRequest[A]] = build(request, OrgAuthData.IdentifiedCustomer.fromMap).map(new IdentifiedCustomerRequest(_, request))
    def identifiedOrg[A](request: Request[A]): Option[IdentifiedOrgRequest[A]] = build(request, OrgAuthData.Identified.fromMap).map(new IdentifiedOrgRequest(_, request))
    def identified[A](request: Request[A]): Option[IdentifiedRequest[A]] = build(request, AuthData.Identified.fromMap).map(new IdentifiedRequest(_, request))
    def org[A](request: Request[A]): Option[OrgRequest[A]] = build(request, OrgAuthData.Org.fromMap).map(new OrgRequest(_, request))
    def sessionOrg[A](request: Request[A]): Option[SessionOrgRequest[A]] = build(request, OrgAuthData.Session.fromMap).map(new SessionOrgRequest(_, request))
    def session[A](request: Request[A]): Option[SessionRequest[A]] = build(request, AuthData.Session.fromMap).map(new SessionRequest(_, request))
    def identifiedChannel[A](request: Request[A]): Option[IdentifiedChannelRequest[A]] = build(request, ChannelAuthData.IdentifiedChannel.fromMap).map(new IdentifiedChannelRequest(_, request))

}
