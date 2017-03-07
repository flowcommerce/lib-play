package io.flow.play.controllers

import io.flow.common.v0.models.UserReference
import io.flow.play.util.AuthData
import io.flow.token.v0.errors.UnitResponse
import io.flow.token.v0.models._
import java.util.UUID
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.Headers
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * @deprecated Use AuthDataFromFlowAuthHeader
  */
trait UserFromFlowAuth extends AuthDataFromFlowAuthHeader {

  /**
    * Uses the x-flow-auth header if present; otherwise defaults to
    * legacy method of validating the token directly.
    */
  override def auth(
    headers: Headers
  ) (
    implicit ec: ExecutionContext
  ): Option[AuthData] = {
    super.auth(headers) match {
      case Some(data) => Some(data)
      case None => {
        Await.result(
          legacyUser(headers),
          Duration(5, "seconds")
        ).map { u =>
          val requestId: String = headers.get("X-Flow-Request-Id").getOrElse("lib-play-depr-" + UUID.randomUUID.toString)
          AuthData(
            requestId = requestId,
            createdAt = new DateTime(),
            user = u,
            organization = None
          )
        }
      }
    }
  }

  // Everything below will be removed after all applications are
  // upgraded to use X-Flow-Auth header.
  import io.flow.token.v0.interfaces.{Client => TokenClient}

  def tokenClient: TokenClient

  private[this] def legacyUser(
    headers: Headers
  ) (
    implicit ec: ExecutionContext
  ): Future[Option[UserReference]] = {
    basicAuthorizationToken(headers) match {
      case None => Future(None)
      case Some(token) => {
        token match {
          case token: Authorization.Token => {
            val form = TokenAuthenticationForm(token = token.token)
            tokenClient.tokens.postAuthentications(form).
              map { t => selectUser(t) }.
              recover {
                case UnitResponse(404) => None
                case ex: Throwable => {
                  val msg = s"Error communicating with token service at ${tokenClient.baseUrl}: $ex"
                  throw new Exception(msg, ex)
                }
              }
          }
          case token: Authorization.JwtToken => {
            Future(
              Some(UserReference(token.userId))
            )
          }
        }
      }
    }
  }

  /**
    * If present, parses the basic authorization header and returns
    * its decoded value.
    */
  private[this] def basicAuthorizationToken(
    headers: play.api.mvc.Headers
  ): Option[Authorization] = {
    headers.get("Authorization").flatMap { h =>
      Authorization.get(h)
    }
  }

  private[this] def selectUser(token: TokenReference): Option[UserReference] = {
    token match {
      case t: OrganizationTokenReference => Some(t.user)
      case t: PartnerTokenReference => Some(t.user)
      case TokenReferenceUndefinedType(other) => {
        Logger.warn(s"TokenReferenceUndefinedType($other) - assuming no user")
        None
      }
    }
  }
  
}
