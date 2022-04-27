package io.flow.play.controllers

import javax.inject.Inject
import com.google.inject.ImplementedBy
import io.flow.common.v0.models.UserReference
import io.flow.log.RollbarLogger
import io.flow.play.util.{AuthHeaders, Config, RequestFactory}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  USAGE:
  --------------------------------------------------------------
  1) Extend play controller with FlowController
  2) In controller's constructor provide the following for DI
      val controllerComponents: ControllerComponents,
      val flowControllerComponents: FlowControllerComponents

  If you want the components to be set automatically by DI, you can extend [[InjectedFlowController]] instead.
*/
trait FlowController extends BaseController with FlowControllerHelpers {
  protected def flowControllerComponents: FlowControllerComponents

  def Anonymous: AnonymousActionBuilder = flowControllerComponents.anonymousActionBuilder
  def Identified: IdentifiedActionBuilder = flowControllerComponents.identifiedActionBuilder
  def Session: SessionActionBuilder = flowControllerComponents.sessionActionBuilder
  def Customer: CustomerActionBuilder = flowControllerComponents.customerActionBuilder
  def Org: OrgActionBuilder = flowControllerComponents.orgActionBuilder
  def IdentifiedOrg: IdentifiedOrgActionBuilder = flowControllerComponents.identifiedOrgActionBuilder
  def SessionOrg: SessionOrgActionBuilder = flowControllerComponents.sessionOrgActionBuilder
  def IdentifiedCookie: IdentifiedCookieActionBuilder = flowControllerComponents.identifiedCookieActionBuilder
  def CustomerOrg: CustomerOrgActionBuilder = flowControllerComponents.customerOrgActionBuilder
  def Checkout: CheckoutActionBuilder = flowControllerComponents.checkoutActionBuilder
  def CheckoutOrg: CheckoutOrgActionBuilder = flowControllerComponents.checkoutOrgActionBuilder
  def IdentifiedCustomer: IdentifiedCustomerActionBuilder = flowControllerComponents.identifiedCustomerActionBuilder
  def IdentifiedChannel: IdentifiedChannelActionBuilder = flowControllerComponents.identifiedChannelActionBuilder
  def OrganizationV2: OrganizationV2ActionBuilder = flowControllerComponents.organizationV2ActionBuilder
}

@ImplementedBy(classOf[FlowDefaultControllerComponents])
trait FlowControllerComponents {
  def anonymousActionBuilder: AnonymousActionBuilder
  def identifiedActionBuilder: IdentifiedActionBuilder
  def sessionActionBuilder: SessionActionBuilder
  def customerActionBuilder: CustomerActionBuilder
  def orgActionBuilder: OrgActionBuilder
  def identifiedOrgActionBuilder: IdentifiedOrgActionBuilder
  def sessionOrgActionBuilder: SessionOrgActionBuilder
  def identifiedCookieActionBuilder: IdentifiedCookieActionBuilder
  def customerOrgActionBuilder: CustomerOrgActionBuilder
  def checkoutActionBuilder: CheckoutActionBuilder
  def checkoutOrgActionBuilder: CheckoutOrgActionBuilder
  def identifiedCustomerActionBuilder: IdentifiedCustomerActionBuilder
  def identifiedChannelActionBuilder: IdentifiedChannelActionBuilder
  def organizationV2ActionBuilder: OrganizationV2ActionBuilder
}

case class FlowDefaultControllerComponents @Inject()(
  anonymousActionBuilder: AnonymousActionBuilder,
  identifiedActionBuilder: IdentifiedActionBuilder,
  sessionActionBuilder: SessionActionBuilder,
  customerActionBuilder: CustomerActionBuilder,
  orgActionBuilder: OrgActionBuilder,
  identifiedOrgActionBuilder: IdentifiedOrgActionBuilder,
  sessionOrgActionBuilder: SessionOrgActionBuilder,
  identifiedCookieActionBuilder: IdentifiedCookieActionBuilder,
  customerOrgActionBuilder: CustomerOrgActionBuilder,
  checkoutActionBuilder: CheckoutActionBuilder,
  checkoutOrgActionBuilder: CheckoutOrgActionBuilder,
  identifiedCustomerActionBuilder: IdentifiedCustomerActionBuilder,
  identifiedChannelActionBuilder: IdentifiedChannelActionBuilder,
  organizationV2ActionBuilder: OrganizationV2ActionBuilder,
) extends FlowControllerComponents

// Anonymous
class AnonymousActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AnonymousRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
    val flowRequest = requestFactory.anonymous(request)
    block(flowRequest)
  }
}

// Identified
class IdentifiedActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.identified(request).fold(Future.successful(unauthorized(request)))(block)
}

// Session
class SessionActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.session(request).fold(Future.successful(unauthorized(request)))(block)
}

// Customer
class CustomerActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CustomerRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CustomerRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.customer(request).fold(Future.successful(unauthorized(request)))(block)
}

// Org
class OrgActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.org(request).fold(Future.successful(unauthorized(request)))(block)
}

// Checkout
class CheckoutActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CheckoutRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CheckoutRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.checkout(request).fold(Future.successful(unauthorized(request)))(block)
}

// CheckoutOrg
class CheckoutOrgActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CheckoutOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CheckoutOrgRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.checkoutOrg(request).fold(Future.successful(unauthorized(request)))(block)
}

// IdentifiedOrg
class IdentifiedOrgActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.identifiedOrg(request).fold(Future.successful(unauthorized(request)))(block)
}

// SessionOrg
class SessionOrgActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.sessionOrg(request).fold(Future.successful(unauthorized(request)))(block)
}

// IdentifiedCookie
class IdentifiedCookieActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    request.session.get(IdentifiedCookie.UserKey) match {
      case None => Future.successful(unauthorized(request))
      case Some(userId) =>
        val auth = AuthHeaders.user(UserReference(id = userId))
        block(new IdentifiedRequest(auth, request))
    }
}

// IdentifiedChannel
class IdentifiedChannelActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedChannelRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedChannelRequest[A]) => Future[Result]): Future[Result] =
    requestFactory.identifiedChannel(request).fold(Future.successful(unauthorized(request)))(block)
}

// OrganizationV2
class OrganizationV2ActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrganizationV2Request, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrganizationV2Request[A]) => Future[Result]): Future[Result] =
    requestFactory.organizationV2(request).fold(Future.successful(unauthorized(request)))(block)
}

object IdentifiedCookie {

  val UserKey = "user_id"

  implicit class ResultWithUser(val result: Result) extends AnyVal {
    def withIdentifiedCookieUser(user: UserReference): Result = result.withSession(UserKey -> user.id.toString)
  }

}

// CustomerOrg
class CustomerOrgActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config)(
  implicit private val logger: RollbarLogger,
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[CustomerOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: CustomerOrgRequest[A] => Future[Result]): Future[Result] =
    requestFactory.customerOrg(request).fold(Future.successful(unauthorized(request)))(block)
}

// IdentifiedCustomer
class IdentifiedCustomerActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config)(
  implicit private val logger: RollbarLogger,
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[IdentifiedCustomerRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: IdentifiedCustomerRequest[A] => Future[Result]): Future[Result] =
    requestFactory.identifiedCustomer(request).fold(Future.successful(unauthorized(request)))(block)
}

// OrganizationV2
class OrganizationV2ChannelActionBuilder @Inject()(requestFactory: RequestFactory, val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrganizationV2Request, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrganizationV2Request[A]) => Future[Result]): Future[Result] =
    requestFactory.organizationV2(request).fold(Future.successful(unauthorized(request)))(block)
}
