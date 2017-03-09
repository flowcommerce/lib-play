package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import io.flow.common.v0.models.{Environment, UserReference}
import org.joda.time.DateTime
import play.api.mvc.Results.Unauthorized

import scala.concurrent.Future
import play.api.mvc._

class AnonymousRequest[A](
  val auth: AuthData.AnonymousAuth,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: Option[UserReference] = auth.user
}

class AnonymousOrgRequest[A](
  val auth: AuthData.AnonymousOrgAuth,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: Option[UserReference] = auth.user
  val organization: String = auth.orgData.organization
  val environment: Environment = auth.orgData.environment
}

class IdentifiedRequest[A](
  val auth: AuthData.IdentifiedAuth,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
}

class IdentifiedOrgRequest[A](
  val auth: AuthData.IdentifiedOrgAuth,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
  val organization: String = auth.orgData.organization
  val environment: Environment = auth.orgData.environment
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

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val ad = auth(request.headers)(AuthData.AnonymousAuth.fromMap).getOrElse {
        // Create an empty header here so at least requestId tracking can start
        AuthData.AnonymousAuth(
          requestId = AuthHeaders.generateRequestId("anonymousrequest"),
          user = None
        )
      }

      block(
        new AnonymousRequest(ad, request)
      )
    }

  }

  object AnonymousOrg extends ActionBuilder[AnonymousOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.AnonymousOrgAuth.fromMap) match {
        case None => Future.successful (
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new AnonymousOrgRequest(ad, request)
          )
        }
      }
    }
  }

  object Identified extends ActionBuilder[IdentifiedRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.IdentifiedAuth.fromMap) match {
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

  object IdentifiedOrg extends ActionBuilder[IdentifiedOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers)(AuthData.IdentifiedOrgAuth.fromMap) match {
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
}