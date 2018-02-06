package io.flow.play.controllers

import javax.inject.Inject

import io.flow.common.v0.models.UserReference
import io.flow.play.util.{AuthData, AuthHeaders, Config, OrgAuthData}
import play.api.inject.Module
import play.api.mvc._
import play.api.{Configuration, Environment}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds


/*
  USAGE:
  --------------------------------------------------------------
  1) Extend play controller with FlowController
  2) In controller's constructor provide the following for DI
      val config: Config,
      val controllerComponents: ControllerComponents,
      val flowControllerComponents: FlowControllerComponents
  3) Enable FlowControllerComponentsModule
    in base.conf
      play.modules.enabled += "io.flow.play.controllers.FlowControllerComponentsModule"
*/
trait FlowController extends BaseController with BaseControllerHelpers with FlowControllerHelpers {
  protected def flowControllerComponents: FlowControllerComponents

  def Anonymous: AnonymousActionBuilder[AnonymousRequest, AnyContent] = flowControllerComponents.anonymousActionBuilder
  def Identified: IdentifiedActionBuilder[IdentifiedRequest, AnyContent] = flowControllerComponents.identifiedActionBuilder
  def Session: SessionActionBuilder[SessionRequest, AnyContent] = flowControllerComponents.sessionActionBuilder
  def Org: OrgActionBuilder[OrgRequest, AnyContent] = flowControllerComponents.orgActionBuilder
  def IdentifiedOrg: IdentifiedOrgActionBuilder[IdentifiedOrgRequest, AnyContent] = flowControllerComponents.identifiedOrgActionBuilder
  def SessionOrg: SessionOrgActionBuilder[SessionOrgRequest, AnyContent] = flowControllerComponents.sessionOrgActionBuilder
  def IdentifiedCookie: IdentifiedCookieActionBuilder[IdentifiedRequest, AnyContent] = flowControllerComponents.identifiedCookieActionBuilder

}

trait FlowControllerComponents {
  def anonymousActionBuilder: AnonymousActionBuilder[AnonymousRequest, AnyContent]
  def identifiedActionBuilder: IdentifiedActionBuilder[IdentifiedRequest, AnyContent]
  def sessionActionBuilder: SessionActionBuilder[SessionRequest, AnyContent]
  def orgActionBuilder: OrgActionBuilder[OrgRequest, AnyContent]
  def identifiedOrgActionBuilder: IdentifiedOrgActionBuilder[IdentifiedOrgRequest, AnyContent]
  def sessionOrgActionBuilder: SessionOrgActionBuilder[SessionOrgRequest, AnyContent]
  def identifiedCookieActionBuilder: IdentifiedCookieActionBuilder[IdentifiedRequest, AnyContent]
}

case class FlowDefaultControllerComponents @Inject() (
  anonymousActionBuilder: AnonymousDefaultActionBuilder,
  identifiedActionBuilder: IdentifiedDefaultActionBuilder,
  sessionActionBuilder: SessionDefaultActionBuilder,
  orgActionBuilder: OrgDefaultActionBuilder,
  identifiedOrgActionBuilder: IdentifiedOrgDefaultActionBuilder,
  sessionOrgActionBuilder: SessionOrgDefaultActionBuilder,
  identifiedCookieActionBuilder: IdentifiedCookieDefaultActionBuilder
) extends FlowControllerComponents

// DI
class FlowControllerComponentsModule extends Module {
  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[FlowControllerComponents].to[FlowDefaultControllerComponents],
      bind[AnonymousDefaultActionBuilder].to[AnonymousDefaultActionBuilderImpl],
      bind[IdentifiedDefaultActionBuilder].to[IdentifiedDefaultActionBuilderImpl],
      bind[SessionDefaultActionBuilder].to[SessionDefaultActionBuilderImpl],
      bind[OrgDefaultActionBuilder].to[OrgDefaultActionBuilderImpl],
      bind[IdentifiedOrgDefaultActionBuilder].to[IdentifiedOrgDefaultActionBuilderImpl],
      bind[SessionOrgDefaultActionBuilder].to[SessionOrgDefaultActionBuilderImpl],
      bind[IdentifiedCookieDefaultActionBuilder].to[IdentifiedCookieDefaultActionBuilderImpl]
    )
  }
}

// Anonymous
trait AnonymousActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait AnonymousDefaultActionBuilder extends AnonymousActionBuilder[AnonymousRequest, AnyContent]
object AnonymousDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): AnonymousDefaultActionBuilder =
    new AnonymousDefaultActionBuilderImpl(parser, config)
}

class AnonymousDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends AnonymousActionBuilderImpl(parser, config) with AnonymousDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class AnonymousActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends AnonymousActionBuilder[AnonymousRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
    val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
      // Create an empty header here so at least requestId tracking can start
      AuthData.Anonymous.Empty
    }
    block(new AnonymousRequest(ad, request))
  }
}

// Identified
trait IdentifiedActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait IdentifiedDefaultActionBuilder extends IdentifiedActionBuilder[IdentifiedRequest, AnyContent]
object IdentifiedDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): IdentifiedDefaultActionBuilder =
    new IdentifiedDefaultActionBuilderImpl(parser, config)
}

class IdentifiedDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends IdentifiedActionBuilderImpl(parser, config) with IdentifiedDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class IdentifiedActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends IdentifiedActionBuilder[IdentifiedRequest, B] with FlowActionInvokeBlockHelper {
  override def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedRequest(ad, request))
  }
}

// Session
trait SessionActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait SessionDefaultActionBuilder extends SessionActionBuilder[SessionRequest, AnyContent]
object SessionDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): SessionDefaultActionBuilder =
    new SessionDefaultActionBuilderImpl(parser, config)
}

class SessionDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends SessionActionBuilderImpl(parser, config) with SessionDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class SessionActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends SessionActionBuilder[SessionRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(AuthData.Session.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new SessionRequest(ad, request))
  }
}

// Org
trait OrgActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait OrgDefaultActionBuilder extends OrgActionBuilder[OrgRequest, AnyContent]
object OrgDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): OrgDefaultActionBuilder =
    new OrgDefaultActionBuilderImpl(parser, config)
}

class OrgDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends OrgActionBuilderImpl(parser, config) with OrgDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class OrgActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends OrgActionBuilder[OrgRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Org.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new OrgRequest(ad, request))
  }
}

// IdentifiedOrg
trait IdentifiedOrgActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait IdentifiedOrgDefaultActionBuilder extends IdentifiedOrgActionBuilder[IdentifiedOrgRequest, AnyContent]
object IdentifiedOrgDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): IdentifiedOrgDefaultActionBuilder =
    new IdentifiedOrgDefaultActionBuilderImpl(parser, config)
}

class IdentifiedOrgDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends IdentifiedOrgActionBuilderImpl(parser, config) with IdentifiedOrgDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class IdentifiedOrgActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends IdentifiedOrgActionBuilder[IdentifiedOrgRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Identified.fromMap) match {
      case None => Future.successful(unauthorized(request))
      case Some(ad) => block(new IdentifiedOrgRequest(ad, request))
  }
}

// SessionOrg
trait SessionOrgActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait SessionOrgDefaultActionBuilder extends SessionOrgActionBuilder[SessionOrgRequest, AnyContent]
object SessionOrgDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): SessionOrgDefaultActionBuilder =
    new SessionOrgDefaultActionBuilderImpl(parser, config)
}

class SessionOrgDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends SessionOrgActionBuilderImpl(parser, config) with SessionOrgDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class SessionOrgActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends SessionOrgActionBuilder[SessionOrgRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] =
    auth(request.headers)(OrgAuthData.Session.fromMap) match {
      case None => Future.successful (unauthorized(request))
      case Some(ad) => block(new SessionOrgRequest(ad, request))
  }
}

// IdentifiedCookie
trait IdentifiedCookieActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait IdentifiedCookieDefaultActionBuilder extends IdentifiedCookieActionBuilder[IdentifiedRequest, AnyContent]
object IdentifiedCookieDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): IdentifiedCookieDefaultActionBuilder =
    new IdentifiedCookieDefaultActionBuilderImpl(parser, config)
}

class IdentifiedCookieDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends IdentifiedCookieActionBuilderImpl(parser, config) with IdentifiedCookieDefaultActionBuilder {
  @Inject def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class IdentifiedCookieActionBuilderImpl[B](val parser: BodyParser[B], val config: Config)(implicit val executionContext: ExecutionContext)
  extends IdentifiedCookieActionBuilder[IdentifiedRequest, B] with FlowActionInvokeBlockHelper {
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
