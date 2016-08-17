package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.util.{AuthData, Config, OrganizationAuthData}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.Logger
import play.api.mvc.{Headers, Session}
import scala.concurrent.{ExecutionContext, Future}

trait AuthDataFromFlowAuthHeader  {

  def config: Config

  def jwtSalt: String = config.requiredString("JWT_SALT")

  private[this] val DefaultAuthExpirationTimeSeconds = 120

  private[this] lazy val authExpirationTimeSeconds = config.optionalInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  
  def auth(headers: Headers) (
    implicit ec: ExecutionContext
  ): Option[AuthData] = {
    headers.get(AuthData.Header).flatMap { parse(_) }
  }

  def parse(value: String): Option[AuthData] = {
    value match {
      case JsonWebToken(header, claimsSet, signature) if jwtIsValid(value) => parseJwtToken(claimsSet)
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken(claimsSet: JwtClaimsSetJValue): Option[AuthData] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      claims.get("user_id").flatMap { userId =>
        claims.get("created_at").flatMap { ts =>
          val createdAt = ISODateTimeFormat.dateTimeParser.parseDateTime(ts)
          val expiration = (new DateTime()).plusSeconds(authExpirationTimeSeconds)
          createdAt.isBefore(expiration) match {
            case false => {
              Logger.warn(s"Flow auth data is expired. CreatedAt[$createdAt] expiration[$expiration] userId[$userId]")
              None
            }

            case true => {
              Some(
                AuthData(
                  createdAt = createdAt,
                  user = UserReference(userId),
                  organization = orgAuthData(claims)
                )
              )
            }
          }
        }
      }
    }
  }

  private[this] def orgAuthData(claims: Map[String, String]): Option[OrganizationAuthData] = {
    claims.get("organization").flatMap { org =>
      (claims.get("role").map { Role(_) }, claims.get("environment").map { Environment(_) }) match {
        case (Some(role), Some(env)) => {
          Some(
            OrganizationAuthData(
              organization = org,
              role = role,
              environment = env
            )
          )
        }
        case (role, env) => {
          Logger.error(s"Flow auth data had an organization specified but missing role[$role] or environment[$env]")
          None
        }
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

            tokenClient.tokens.get(token = Some(token.token)).map(_.headOption.map(_.user)).recover {
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
