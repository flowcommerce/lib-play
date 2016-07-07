package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Role, UserReference}
import io.flow.play.util.{AuthData, Config}
import play.api.mvc.{Headers, Session}

import scala.concurrent.{ExecutionContext, Future}

trait UserFromFlowAuth  {

  def config: Config

  private[this] lazy val jwtSalt = config.requiredString("JWT_SALT")
  
  def user(
    session: Session,
    headers: Headers,
    path: String,
    queryString: Map[String, Seq[String]]
  ) (
    implicit ec: ExecutionContext
  ): Future[Option[UserReference]] = {
    headers.get("X-Flow-Auth") match {
      case None => {
        legacyUser(session, headers, path, queryString)
      }

      case Some(value) => Future {
        value match {
          case JsonWebToken(header, claimsSet, signature) if jwtIsValid(value) => parseJwtToken(claimsSet).map { data =>
            UserReference(data.userId)
          }
          case _ => None
        }
      }
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken(claimsSet: JwtClaimsSetJValue): Option[AuthData] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      claims.get("user_id").map { userId =>
        AuthData(
          userId = userId,
          organization = claims.get("organization"),
          role = claims.get("role").map { Role(_) }
        )
      }
    }
  }

  // Everything below will be removed after all applications are
  // upgraded to use X-Flow-Auth header.
  import io.flow.token.v0.interfaces.{Client => TokenClient}

  def tokenClient: TokenClient

  private[this] def legacyUser(
    session: Session,
    headers: Headers,
    path: String,
    queryString: Map[String, Seq[String]]
  ) (
    implicit ec: ExecutionContext
  ): Future[Option[UserReference]] = {
    basicAuthorizationToken(headers) match {
      case None => Future { None }
      case Some(token) => {
        token match {
          case token: Authorization.Token => {

            tokenClient.tokens.get(token = Seq(token.token)).map(_.headOption.map(_.user)).recover {
              case ex: Throwable => {
                val msg = s"Error communicating with token service at ${tokenClient.baseUrl}: $ex"
                throw new Exception(msg, ex)
              }
            }

          }
          case token: Authorization.JwtToken => {
            Future {
              Some(UserReference(token.userId))
            }
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

}
