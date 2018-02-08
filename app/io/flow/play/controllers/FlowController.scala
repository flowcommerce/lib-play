package io.flow.play.controllers

import javax.inject.Inject

import com.google.inject.ImplementedBy
import io.flow.common.v0.models.UserReference
import io.flow.play.util.MockableApiUtil._
import io.flow.play.util.{AuthData, AuthHeaders, Config, OrgAuthData}
import play.api.inject.{Binding, Module}
import play.api.mvc._
import play.api.{Configuration, Environment}

import scala.concurrent.{ExecutionContext, Future}

/*
  USAGE:
  --------------------------------------------------------------
  1) Extend play controller with FlowController
  2) In controller's constructor provide the following for DI
      val config: Config,
      val controllerComponents: ControllerComponents,
      val flowControllerComponents: FlowControllerComponents
*/
trait FlowController extends BaseController with BaseControllerHelpers with FlowControllerHelpers {
  protected def flowControllerComponents: FlowControllerComponents

  def Anonymous: AnonymousActionBuilder = flowControllerComponents.anonymousActionBuilder

  def Identified: IdentifiedActionBuilder = flowControllerComponents.identifiedActionBuilder

  def Session: SessionActionBuilder = flowControllerComponents.sessionActionBuilder

  def Org: OrgActionBuilder = flowControllerComponents.orgActionBuilder

  def IdentifiedOrg: IdentifiedOrgActionBuilder = flowControllerComponents.identifiedOrgActionBuilder

  def SessionOrg: SessionOrgActionBuilder = flowControllerComponents.sessionOrgActionBuilder

  def IdentifiedCookie: IdentifiedCookieActionBuilder = flowControllerComponents.identifiedCookieActionBuilder


  def MockableAnonymous: MockableAnonymousActionBuilder = flowControllerComponents.mockableAnonymousActionBuilder

  def MockableIdentified: MockableIdentifiedActionBuilder = flowControllerComponents.mockableIdentifiedActionBuilder

  def MockableSession: MockableSessionActionBuilder = flowControllerComponents.mockableSessionActionBuilder

  def MockableOrg: MockableOrgActionBuilder = flowControllerComponents.mockableOrgActionBuilder

  def MockableIdentifiedOrg: MockableIdentifiedOrgActionBuilder = flowControllerComponents.mockableIdentifiedOrgActionBuilder

  def MockableSessionOrg: MockableSessionOrgActionBuilder = flowControllerComponents.mockableSessionOrgActionBuilder

  def MockableIdentifiedCookie: MockableIdentifiedCookieActionBuilder = flowControllerComponents.mockableIdentifiedCookieActionBuilder

}

@ImplementedBy(classOf[FlowDefaultControllerComponents])
trait FlowControllerComponents {
  def anonymousActionBuilder: AnonymousActionBuilder

  def identifiedActionBuilder: IdentifiedActionBuilder

  def sessionActionBuilder: SessionActionBuilder

  def orgActionBuilder: OrgActionBuilder

  def identifiedOrgActionBuilder: IdentifiedOrgActionBuilder

  def sessionOrgActionBuilder: SessionOrgActionBuilder

  def identifiedCookieActionBuilder: IdentifiedCookieActionBuilder


  def mockableAnonymousActionBuilder: MockableAnonymousActionBuilder

  def mockableIdentifiedActionBuilder: MockableIdentifiedActionBuilder

  def mockableSessionActionBuilder: MockableSessionActionBuilder

  def mockableOrgActionBuilder: MockableOrgActionBuilder

  def mockableIdentifiedOrgActionBuilder: MockableIdentifiedOrgActionBuilder

  def mockableSessionOrgActionBuilder: MockableSessionOrgActionBuilder

  def mockableIdentifiedCookieActionBuilder: MockableIdentifiedCookieActionBuilder
}

case class FlowDefaultControllerComponents @Inject()(
  anonymousActionBuilder: AnonymousActionBuilder,
  identifiedActionBuilder: IdentifiedActionBuilder,
  sessionActionBuilder: SessionActionBuilder,
  orgActionBuilder: OrgActionBuilder,
  identifiedOrgActionBuilder: IdentifiedOrgActionBuilder,
  sessionOrgActionBuilder: SessionOrgActionBuilder,
  identifiedCookieActionBuilder: IdentifiedCookieActionBuilder,

  mockableAnonymousActionBuilder: MockableAnonymousActionBuilder,
  mockableIdentifiedActionBuilder: MockableIdentifiedActionBuilder,
  mockableSessionActionBuilder: MockableSessionActionBuilder,
  mockableOrgActionBuilder: MockableOrgActionBuilder,
  mockableIdentifiedOrgActionBuilder: MockableIdentifiedOrgActionBuilder,
  mockableSessionOrgActionBuilder: MockableSessionOrgActionBuilder,
  mockableIdentifiedCookieActionBuilder: MockableIdentifiedCookieActionBuilder

) extends FlowControllerComponents

// Used to be DI
@deprecated("This module does not do anything any more. Please remove it from your bindings.", since = "0.4.39")
class FlowControllerComponentsModule extends Module {
  def bindings(env: Environment, conf: Configuration): Seq[Binding[_]] = Seq.empty[Binding[_]]
}

// Anonymous
class AnonymousActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
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
class IdentifiedActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedRequest(ad, request))
    }
}

// Session
class SessionActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Session.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new SessionRequest(ad, request))
    }
}

// Org
class OrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Org.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new OrgRequest(ad, request))
    }
}

// IdentifiedOrg
class IdentifiedOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedOrgRequest(ad, request))
    }
}

// SessionOrg
class SessionOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
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




// Mockable Anonymous
class MockableAnonymousActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AnonymousRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
    withMockableApis(request) {
      val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
        // Create an empty header here so at least requestId tracking can start
        AuthData.Anonymous.Empty
      }
      block(new AnonymousRequest(ad, request))
    }
  }
}

// Mockable Identified
class MockableIdentifiedActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      auth(request.headers)(AuthData.Identified.fromMap) match {
        case None => Future.successful(unauthorized(request))
        case Some(ad) => block(new IdentifiedRequest(ad, request))
      }
    }
}

// Mockable Session
class MockableSessionActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      auth(request.headers)(AuthData.Session.fromMap) match {
        case None => Future.successful(unauthorized(request))
        case Some(ad) => block(new SessionRequest(ad, request))
      }
    }
}

// Mockable Org
class MockableOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      auth(request.headers)(OrgAuthData.Org.fromMap) match {
        case None => Future.successful(unauthorized(request))
        case Some(ad) => block(new OrgRequest(ad, request))
      }
    }
}

// Mockable IdentifiedOrg
class MockableIdentifiedOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      auth(request.headers)(OrgAuthData.Identified.fromMap) match {
        case None => Future.successful(unauthorized(request))
        case Some(ad) => block(new IdentifiedOrgRequest(ad, request))
      }
    }
}

// Mockable SessionOrg
class MockableSessionOrgActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      auth(request.headers)(OrgAuthData.Session.fromMap) match {
        case None => Future.successful(unauthorized(request))
        case Some(ad) => block(new SessionOrgRequest(ad, request))
      }
    }
}

// Mockable IdentifiedCookie
class MockableIdentifiedCookieActionBuilder @Inject()(val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    withMockableApis(request) {
      request.session.get(IdentifiedCookie.UserKey) match {
        case None => Future.successful(unauthorized(request))
        case Some(userId) =>
          val auth = AuthHeaders.user(UserReference(id = userId))
          block(new IdentifiedRequest(auth, request))
      }
    }
}