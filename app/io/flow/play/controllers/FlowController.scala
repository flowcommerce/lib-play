package io.flow.play.controllers

import java.util.concurrent.{Executor, Executors}

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import common.{CommonDefaults, FlowAction, WsStandaloneClient}
import io.flow.play.util._
import io.flow.common.v0.models.{Environment, UserReference}
import play.api.mvc.Results.Unauthorized

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc._

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


/**
  * Primarily a marker to indicate intention to make all actions in a
  * controller anonymous. Also includes a few helper methods to interact
  * with users - intended to allow an anonymous action to succeed in cases
  * where we may or may not have a user.
  */
trait FlowController extends FlowControllerHelpers {

  def config: Config

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt: String = config.requiredString("JWT_SALT")

  private[this] val DefaultAuthExpirationTimeSeconds = 180

  private[this] lazy val authExpirationTimeSeconds = {
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  }

  private[this] def auth[T <: AuthData](headers: Headers)(
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

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken[T <: AuthData](claimsSet: JwtClaimsSetJValue)(
    f: Map[String, String] => Option[T]
  ): Option[T] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      f(claims).filter { auth =>
        auth.createdAt.plusSeconds(authExpirationTimeSeconds).isAfterNow
      }
    }
  }

  object AnonymousT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new AnonymousT[B]
  }

  class AnonymousT[B](implicit ec: ExecutionContext,
                      bodyParser: BodyParser[B]) extends ActionBuilder[AnonymousRequest, B] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
        // Create an empty header here so at least requestId tracking can start
        AuthData.Anonymous.Empty
      }

      block(
        new AnonymousRequest(ad, request)
      )
    }

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object Anonymous {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new Anonymous
    def request = apply
  }

  class Anonymous(implicit ec: ExecutionContext) extends FlowAction[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val ad = auth(request.headers)(AuthData.Anonymous.fromMap).getOrElse {
        // Create an empty header here so at least requestId tracking can start
        AuthData.Anonymous.Empty
      }

      block(
        new AnonymousRequest(ad, request)
      )
    }
  }

  object SessionOrgT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new SessionOrgT[B]
  }

  class SessionOrgT[B](implicit ec: ExecutionContext,
                       bodyParser: BodyParser[B]) extends ActionBuilder[SessionOrgRequest, B] {

    def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Session.fromMap) match {
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

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object SessionOrg {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new SessionOrg
    def request = apply
  }

  class SessionOrg(implicit ec: ExecutionContext) extends FlowAction[SessionOrgRequest] {

    override def invokeBlock[A](request: Request[A], block: (SessionOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Session.fromMap) match {
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

  object IdentifiedT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new IdentifiedT[B]
  }

  class IdentifiedT[B](implicit ec: ExecutionContext,
                      bodyParser: BodyParser[B]) extends ActionBuilder[IdentifiedRequest, B] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.Identified.fromMap) match {
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

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object Identified {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new Identified
    def request = apply
  }

  class Identified(implicit ec: ExecutionContext) extends FlowAction[IdentifiedRequest] {

    override def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.Identified.fromMap) match {
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

  object SessionT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new SessionT[B]
  }

  class SessionT[B](implicit ec: ExecutionContext,
                   bodyParser: BodyParser[B]) extends ActionBuilder[SessionRequest, B] {

    def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.Session.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new SessionRequest(ad, request)
          )
        }
      }
    }

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object Session {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new Session
    def request = apply
  }

  class Session(implicit ec: ExecutionContext) extends FlowAction[SessionRequest]{

    def invokeBlock[A](request: Request[A], block: (SessionRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.Session.fromMap) match {
        case None => Future.successful(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new SessionRequest(ad, request)
          )
        }
      }
    }

    override protected def executionContext: ExecutionContext = ec
  }

  object IdentifiedOrgT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new IdentifiedOrgT[B]
  }

  class IdentifiedOrgT[B](implicit ec: ExecutionContext,
                         bodyParser: BodyParser[B]) extends ActionBuilder[IdentifiedOrgRequest, B] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Identified.fromMap) match {
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

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object IdentifiedOrg {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new IdentifiedOrg
    def request = apply
  }

  class IdentifiedOrg(implicit ec: ExecutionContext) extends FlowAction[IdentifiedOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Identified.fromMap) match {
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

  object OrgT {
    def apply[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) = new OrgT[B]
  }

  class OrgT[B](implicit ec: ExecutionContext, bodyParser: BodyParser[B]) extends ActionBuilder[OrgRequest, B] {

    def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Org.fromMap) match {
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

    override def parser: BodyParser[B] = bodyParser

    override protected def executionContext: ExecutionContext = ec
  }

  object Org {
    implicit lazy val ec: ExecutionContext = implicitly[ExecutionContext]
    def apply = new Org
    def request = apply
  }

  class Org(implicit ec: ExecutionContext) extends FlowAction[OrgRequest] {

    def invokeBlock[A](request: Request[A], block: (OrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(OrgAuthData.Org.fromMap) match {
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