package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import io.flow.play.util.{AuthData, AuthHeaders, Config, OrganizationAuthData}
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

  private[this] lazy val authExpirationTimeSeconds = {
    config.optionalInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  }
  
  def auth(headers: Headers) (
    implicit ec: ExecutionContext
  ): Option[AuthData] = {
    headers.get(AuthHeaders.Header).flatMap { parse }
  }

  def parse(value: String): Option[AuthData] = {
    value match {
      case JsonWebToken(_, claimsSet, _) if jwtIsValid(value) => parseJwtToken(claimsSet)
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken(claimsSet: JwtClaimsSetJValue): Option[AuthData] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      AuthData.fromMap(claims).filter { auth =>
        val expiration = DateTime.now.plusSeconds(authExpirationTimeSeconds)
        auth.createdAt.isBefore(expiration)
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

}
