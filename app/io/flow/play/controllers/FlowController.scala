package io.flow.play.controllers

import scala.language.higherKinds
import javax.inject.Inject

import io.flow.play.util.{AuthData, Config, OrgAuthData}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.inject.Module
import play.api.{Configuration, Environment}

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
trait FlowController extends BaseController with BaseControllerHelpers {
  protected def flowControllerComponents: FlowControllerComponents

  def Anonymous: AnonymousActionBuilder[AnonymousRequest, AnyContent] = flowControllerComponents.anonymousActionBuilder
  def Identified: IdentifiedActionBuilder[IdentifiedRequest, AnyContent] = flowControllerComponents.identifiedActionBuilder
  def Session: SessionActionBuilder[SessionRequest, AnyContent] = flowControllerComponents.sessionActionBuilder
  def Org: OrgActionBuilder[OrgRequest, AnyContent] = flowControllerComponents.orgActionBuilder
  def IdentifiedOrg: IdentifiedOrgActionBuilder[IdentifiedOrgRequest, AnyContent] = flowControllerComponents.identifiedOrgActionBuilder
  def SessionOrg: SessionOrgActionBuilder[SessionOrgRequest, AnyContent] = flowControllerComponents.sessionOrgActionBuilder
}

trait FlowControllerComponents {
  def anonymousActionBuilder: AnonymousActionBuilder[AnonymousRequest, AnyContent]
  def identifiedActionBuilder: IdentifiedActionBuilder[IdentifiedRequest, AnyContent]
  def sessionActionBuilder: SessionActionBuilder[SessionRequest, AnyContent]
  def orgActionBuilder: OrgActionBuilder[OrgRequest, AnyContent]
  def identifiedOrgActionBuilder: IdentifiedOrgActionBuilder[IdentifiedOrgRequest, AnyContent]
  def sessionOrgActionBuilder: SessionOrgActionBuilder[SessionOrgRequest, AnyContent]
}

case class FlowDefaultControllerComponents @Inject() (
   anonymousActionBuilder: AnonymousDefaultActionBuilder,
   identifiedActionBuilder: IdentifiedDefaultActionBuilder,
   sessionActionBuilder: SessionDefaultActionBuilder,
   orgActionBuilder: OrgDefaultActionBuilder,
   identifiedOrgActionBuilder: IdentifiedOrgDefaultActionBuilder,
   sessionOrgActionBuilder: SessionOrgDefaultActionBuilder) extends FlowControllerComponents

// DI
class FlowControllerComponentsModule extends Module {
  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[FlowControllerComponents].to[FlowDefaultControllerComponents],
      bind[AnonymousDefaultActionBuilder].to[AnonymousDefaultActionBuilderImpl],
      bind[IdentifiedDefaultActionBuilder].to[IdentifiedDefaultActionBuilderImpl],
      bind[OrgDefaultActionBuilder].to[OrgDefaultActionBuilderImpl],
      bind[IdentifiedOrgDefaultActionBuilder].to[IdentifiedOrgDefaultActionBuilderImpl],
      bind[SessionOrgDefaultActionBuilder].to[SessionOrgDefaultActionBuilderImpl]
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