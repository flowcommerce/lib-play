package io.flow.play.controllers

import javax.inject.Inject

import com.google.inject.ImplementedBy
import io.flow.common.v0.models.UserReference
import io.flow.play.util.{AuthData, AuthHeaders, Config, OrgAuthData}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

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
  def Org: OrgActionBuilder = flowControllerComponents.orgActionBuilder
  def IdentifiedOrg: IdentifiedOrgActionBuilder = flowControllerComponents.identifiedOrgActionBuilder
  def SessionOrg: SessionOrgActionBuilder = flowControllerComponents.sessionOrgActionBuilder
  def IdentifiedCookie: IdentifiedCookieActionBuilder = flowControllerComponents.identifiedCookieActionBuilder

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
}

case class FlowDefaultControllerComponents @Inject() (
  anonymousActionBuilder: AnonymousActionBuilder,
  identifiedActionBuilder: IdentifiedActionBuilder,
  sessionActionBuilder: SessionActionBuilder,
  orgActionBuilder: OrgActionBuilder,
  identifiedOrgActionBuilder: IdentifiedOrgActionBuilder,
  sessionOrgActionBuilder: SessionOrgActionBuilder,
  identifiedCookieActionBuilder: IdentifiedCookieActionBuilder
) extends FlowControllerComponents


// Anonymous
class AnonymousActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
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
class IdentifiedActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedRequest(ad, request))
    }
}

// Session
class SessionActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Session.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new SessionRequest(ad, request))
    }
}

// Org
class OrgActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[OrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Org.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new OrgRequest(ad, request))
    }
}

// IdentifiedOrg
class IdentifiedOrgActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifiedOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedOrgRequest(ad, request))
    }
}

// SessionOrg
class SessionOrgActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[SessionOrgRequest, AnyContent] with FlowActionInvokeBlockHelper {

  def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Session.fromMap) match {
      case None => Future.successful (unauthorized(request))
      case Some(ad) => block(new SessionOrgRequest(ad, request))
    }
}

// IdentifiedCookie
class IdentifiedCookieActionBuilder @Inject() (val parser: BodyParsers.Default, val config: Config)(implicit val executionContext: ExecutionContext)
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
    def withIdentifiedCookieUser(user: UserReference) = result.withSession(UserKey -> user.id.toString)
  }

}
