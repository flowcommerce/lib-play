package io.flow.play.controllers

import javax.inject.Inject
import com.google.inject.ImplementedBy
import io.flow.common.v0.models.UserReference
import io.flow.log.RollbarLogger
import io.flow.play.util.{AuthData, AuthHeaders, Config, OrgAuthData}
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
  identifiedCustomerActionBuilder: IdentifiedCustomerActionBuilder
) extends FlowControllerComponents

// Anonymous
class AnonymousActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AnonymousRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
    val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
      // Create an empty header here so at least requestId tracking can start
      AuthData.Anonymous.Empty
    }
    block(new AnonymousRequest(ad, request))
  }
}

// Identified
class IdentifiedActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedRequest(ad, request))
    }
}

// Session
class SessionActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Session.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new SessionRequest(ad, request))
    }
}

// Customer
class CustomerActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CustomerRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CustomerRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Customer.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new CustomerRequest(ad, request))
    }
}

// Org
class OrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Org.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new OrgRequest(ad, request))
    }
}

// Checkout
class CheckoutActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CheckoutRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CheckoutRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Checkout.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new CheckoutRequest(ad, request))
    }
}

// CheckoutOrg
class CheckoutOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CheckoutOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (CheckoutOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.CheckoutOrg.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new CheckoutOrgRequest(ad, request))
    }
}

// IdentifiedOrg
class IdentifiedOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedOrgRequest(ad, request))
    }
}

// SessionOrg
class SessionOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config, implicit private val logger: RollbarLogger)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Session.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new SessionOrgRequest(ad, request))
    }
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

object IdentifiedCookie {

  val UserKey = "user_id"

  implicit class ResultWithUser(val result: Result) extends AnyVal {
    def withIdentifiedCookieUser(user: UserReference): Result = result.withSession(UserKey -> user.id.toString)
  }

}

// CustomerOrg
class CustomerOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(
  implicit private val logger: RollbarLogger,
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[CustomerOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: CustomerOrgRequest[A] => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Customer.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new CustomerOrgRequest(ad, request))
    }
}

// IdentifiedCustomer
class IdentifiedCustomerActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(
  implicit private val logger: RollbarLogger,
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[IdentifiedCustomerRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: IdentifiedCustomerRequest[A] => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.IdentifiedCustomer.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedCustomerRequest(ad, request))
    }
}
