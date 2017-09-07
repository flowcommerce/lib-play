package io.flow.play.controllers

import javax.inject.Inject


import io.flow.play.util.{AuthData, Config}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait AnonymousActionBuilder[+R[_], B] extends ActionBuilder[R, B]

trait FlowBaseController extends BaseController with BaseControllerHelpers {
  protected def flowControllerComponents: FlowControllerComponents

  def AnonymousAction: AnonymousActionBuilder[AnonymousRequest, AnyContent] = flowControllerComponents.anonymousActionBuilder
}

trait FlowControllerComponents {
  def anonymousActionBuilder: AnonymousActionBuilder[AnonymousRequest, AnyContent]
}

case class FlowDefaultControllerComponents @Inject() (anonymousActionBuilder: AnonymousDefaultActionBuilder) extends FlowControllerComponents

trait AnonymousDefaultActionBuilder extends AnonymousActionBuilder[AnonymousRequest, AnyContent]
object AnonymousDefaultActionBuilder {
  def apply(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext): AnonymousDefaultActionBuilder =
    new AnonymousDefaultActionBuilderImpl(parser, config)
}

class AnonymousDefaultActionBuilderImpl(parser: BodyParser[AnyContent], config: Config)(implicit ec: ExecutionContext)
  extends AnonymousActionBuilderImpl(parser, config) with AnonymousDefaultActionBuilder {
  @Inject
  def this(parser: BodyParsers.Default, config: Config)(implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
}

class AnonymousActionBuilderImpl[B](val parser: BodyParser[B], c: Config)(implicit val executionContext: ExecutionContext)
  extends AnonymousActionBuilder[AnonymousRequest, B] with FlowActionInvokeBlockHelper {
  def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
    val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
      // Create an empty header here so at least requestId tracking can start
      AuthData.Anonymous.Empty
    }

    block(
      new AnonymousRequest(ad, request)
    )
  }

  override def config = c
}