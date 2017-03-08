package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.play.util._
import org.joda.time.DateTime
import play.api.mvc.Headers
import scala.concurrent.ExecutionContext

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
      AuthDataMap.fromMap(claims).filter { auth =>
        val expiration = DateTime.now.plusSeconds(authExpirationTimeSeconds)
        auth.createdAt.isBefore(expiration)
      }
    }
  }

  // Everything below will be removed after all applications are
  // upgraded to use X-Flow-Auth header.
  def tokenClient: io.flow.token.v0.interfaces.Client

}
