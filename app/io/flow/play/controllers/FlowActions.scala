package io.flow.play.controllers

import javax.inject.Inject

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Environment, UserReference}
import io.flow.play.util._
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{ActionBuilderImpl, _}

import scala.concurrent.{ExecutionContext, Future}

class AnonymousRequest[A](
                           val auth: AuthData.Anonymous,
                           request: Request[A]
                         ) extends WrappedRequest[A](request) {
  val user: Option[UserReference] = auth.user
}

class SessionOrgRequest[A](
                            val auth: OrgAuthData.Session,
                            request: Request[A]
                          ) extends WrappedRequest[A](request) {
  val flowSession: FlowSession = auth.session
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

class IdentifiedRequest[A](
                            val auth: AuthData.Identified,
                            request: Request[A]
                          ) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
}

class SessionRequest[A](
                         val auth: AuthData.Session,
                         request: Request[A]
                       ) extends WrappedRequest[A](request) {
  val flowSession: FlowSession = auth.session
}

class IdentifiedOrgRequest[A](
                               val auth: OrgAuthData.Identified,
                               request: Request[A]
                             ) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

/**
  * Any type of request that contains org data
  */
class OrgRequest[A](
                     val auth: OrgAuthData,
                     request: Request[A]
                   ) extends WrappedRequest[A](request) {
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}




trait FlowActionInvokeBlockHelper {
  def config: Config

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt: String = config.requiredString("JWT_SALT")

  protected val DefaultAuthExpirationTimeSeconds = 180

  protected lazy val authExpirationTimeSeconds = {
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  }

  protected def auth[T <: AuthData](headers: Headers)(
    f: Map[String, String] => Option[T]
  ): Option[T] = {
    headers.get(AuthHeaders.Header).flatMap { v => parse(v)(f) }
  }

  def parse[T <: AuthData](value: String)(
    f: Map[String, String] => Option[T]
  ): Option[T] = {
    value match {
      case JsonWebToken(_, claimsSet, _) if jwtIsValid(value) => parseJwtToken(claimsSet)(f)
      case _ => None
    }
  }

  protected def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  protected def parseJwtToken[T <: AuthData](claimsSet: JwtClaimsSetJValue)(
    f: Map[String, String] => Option[T]
  ): Option[T] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      f(claims).filter { auth =>
        auth.createdAt.plusSeconds(authExpirationTimeSeconds).isAfterNow
      }
    }
  }
}


//// ANONYMOUS ACTION BUILDER - TODO: Add for all *Action
//
//trait AnonymousActionBuilder extends ActionBuilder[AnonymousRequest, AnyContent]
//
//object AnonymousActionBuilder {
//  def apply(parser: BodyParser[AnyContent], config: Config)
//           (implicit ec: ExecutionContext): AnonymousActionBuilder =
//    new AnonymousActionBuilderImpl(parser, config)
//}
//
//class ActionBuilderImpl[B](val parser: BodyParser[B])
//                          (implicit val executionContext: ExecutionContext)
//  extends ActionBuilder[Request, B] {
//  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)
//}
//
//
//class AnonymousActionBuilderImpl @Inject() (val parser: BodyParser[AnyContent], val config: Config)
//                                (implicit val executionContext: ExecutionContext) extends AnonymousActionBuilder with FlowActionInvokeBlockHelper {
////  @Inject def this(parser: BodyParsers.Default, config: Config)
////                  (implicit ec: ExecutionContext) = this(parser: BodyParser[AnyContent], config: Config)
//
//  override def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
//    val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
//      // Create an empty header here so at least requestId tracking can start
//      AuthData.Anonymous.Empty
//    }
//
//    block(
//      new AnonymousRequest(ad, request)
//    )
//  }
//}
//
//
//// FLOW WIRE UP
////done
//trait FlowBaseControllerHelpers  {
//  protected def flowControllerComponents: FlowControllerComponents
//}
//
////TODO: add *Action
////trait FlowController extends FlowBaseControllerHelpers with BaseController {
////  def AnonymousAction: ActionBuilder[AnonymousRequest, AnyContent] = flowControllerComponents.anonymousActionBuilder
////}
//
////TODO: add *Action
//trait FlowControllerComponents {
//  def anonymousActionBuilder: AnonymousActionBuilder
//}
//
//
//
//abstract class FlowAbstractController(val cc: ControllerComponents,
//                                      val fcc: FlowControllerComponents) extends AbstractController(cc) {
//  def AnonymousAction: ActionBuilder[AnonymousRequest, AnyContent] = fcc.anonymousActionBuilder
//}

// WIRE UP HELPERS

//class FlowControllerImpl(val controllerComponents: ControllerComponents,
//                     val flowControllerComponents: FlowControllerComponents) extends FlowController {
//  def get = AnonymousAction(parse.json) { request =>
//    Ok()
//  }
//
//}

// TODO: remove afterwards
object FlowActions extends FlowControllerHelpers {

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt(config: Config): String = config.requiredString("JWT_SALT")

  private[this] val DefaultAuthExpirationTimeSeconds = 180

  private[this] def authExpirationTimeSeconds(config: Config) = {
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  }

  private[this] def auth[T <: AuthData](config: Config)
                                       (headers: Headers)
                                       (f: Map[String, String] => Option[T]): Option[T] =
    headers.get(AuthHeaders.Header).flatMap { v => parse(config)(v)(f) }

  def parse[T <: AuthData](config: Config)
                          (value: String)
                          (f: Map[String, String] => Option[T]): Option[T] =
    value match {
      case JsonWebToken(_, claimsSet, _) if jwtIsValid(config)(value) => parseJwtToken(config)(claimsSet)(f)
      case _ => None
    }

  private[this] def jwtIsValid(config: Config)(token: String): Boolean = JsonWebToken.validate(token, jwtSalt(config))

  private[this] def parseJwtToken[T <: AuthData](config: Config)
                                                (claimsSet: JwtClaimsSetJValue)
                                                (f: Map[String, String] => Option[T]): Option[T] =
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      f(claims).filter { auth =>
        auth.createdAt.plusSeconds(authExpirationTimeSeconds(config)).isAfterNow
      }
    }

  //  class AnonymousAction[B](val parser: BodyParser[B])
  //                          (config: Config)
  //                          (implicit val executionContext: ExecutionContext) extends ActionBuilder[AnonymousRequest, B] {
  //    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
  //      val ad = auth(config)(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
  //        // Create an empty header here so at least requestId tracking can start
  //        AuthData.Anonymous.Empty
  //      }
  //
  //      block(
  //        new AnonymousRequest(ad, request)
  //      )
  //    }
  //  }

  class AnonymousAction @Inject()(val parser: BodyParsers.Default, config: Config)
                                 (implicit val executionContext: ExecutionContext) extends ActionBuilder[AnonymousRequest, AnyContent] {
    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val ad = auth(config)(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
        // Create an empty header here so at least requestId tracking can start
        AuthData.Anonymous.Empty
      }

      block(
        new AnonymousRequest(ad, request)
      )
    }
  }

  class SessionAction[B] @Inject()(val parser: BodyParser[B], config: Config)
                                  (implicit val executionContext: ExecutionContext) extends ActionBuilder[SessionOrgRequest, B] {
    def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(config)(request.headers)(OrgAuthData.Session.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new SessionOrgRequest(ad, request)
          )
        }
      }
    }
  }


  class IdentifiedAction[B] @Inject()(val parser: BodyParser[B], config: Config)
                                     (implicit val executionContext: ExecutionContext) extends ActionBuilder[IdentifiedRequest, B] {
    override def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      auth(config)(request.headers)(AuthData.Identified.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new IdentifiedRequest(ad, request)
          )
        }
      }
    }
  }


  class SessionOrgAction[B] @Inject()(val parser: BodyParser[B], config: Config)
                                     (implicit val executionContext: ExecutionContext) extends ActionBuilder[SessionOrgRequest, B] {
    def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(config)(request.headers)(OrgAuthData.Session.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new SessionOrgRequest(ad, request)
          )
        }
      }
    }
  }


  class IdentifiedOrgAction[B] @Inject()(val parser: BodyParser[B], config: Config)
                                        (implicit val executionContext: ExecutionContext) extends ActionBuilder[IdentifiedOrgRequest, B] {
    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(config)(request.headers)(OrgAuthData.Identified.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )

        case Some(ad) => {
          block(
            new IdentifiedOrgRequest(ad, request)
          )
        }
      }
    }
  }


  class OrgAction[B] @Inject()(val parser: BodyParser[B], config: Config)
                              (implicit val executionContext: ExecutionContext) extends ActionBuilder[OrgRequest, B] {
    def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(config)(request.headers)(OrgAuthData.Org.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )

        case Some(ad) => {
          block(
            new OrgRequest(ad, request)
          )
        }
      }
    }
  }

}