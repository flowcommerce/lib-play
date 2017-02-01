package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.util.{AuthData, Config, OrganizationAuthData}
import io.flow.token.v0.errors.UnitResponse
import io.flow.token.v0.models._
import java.util.UUID
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.Logger
import play.api.mvc.{Headers, Session}
import scala.concurrent.{ExecutionContext, Future}

trait AuthDataFromFlowAuthHeader  {

  def config: Config

  def jwtSalt: String = config.requiredString("JWT_SALT")

  private[this] val DefaultAuthExpirationTimeSeconds = 180

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
          val requestId = claims.get("request_id").getOrElse {
            Logger.warn("JWT Token did not have a request_id - generated a new request id")
            "lib-play-" + UUID.randomUUID.toString
          }

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
                  requestId = requestId,
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
        case (_, _) => {
          None
        }
      }
    }
  }
  
  // Everything below will be removed after all applications are
  // upgraded to use X-Flow-Auth header.
  import io.flow.token.v0.interfaces.{Client => TokenClient}

  def tokenClient: TokenClient

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
