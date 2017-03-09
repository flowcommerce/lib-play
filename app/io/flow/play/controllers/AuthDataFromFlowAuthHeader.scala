package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.play.util._
import org.joda.time.DateTime
import play.api.mvc.Headers
import scala.concurrent.ExecutionContext

trait AuthDataFromFlowAuthHeader[T <: AuthData]  {

  def config: Config

  // Remove after all applications are upgraded to use X-Flow-Auth header.
  def tokenClient: io.flow.token.v0.interfaces.Client

  /**
    * Override this method to convert a map of incoming headers to
    * the appropriate instance of AuthData
    */
  protected def fromMap(data: Map[String, String]): Option[T]

  def jwtSalt: String = config.requiredString("JWT_SALT")

  private[this] val DefaultAuthExpirationTimeSeconds = 180

  private[this] lazy val authExpirationTimeSeconds = {
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)
  }
  
  def auth(headers: Headers) (
    implicit ec: ExecutionContext
  ): Option[T] = {
    headers.get(AuthHeaders.Header).flatMap { parse }
  }

  def parse(value: String): Option[T] = {
    value match {
      case JsonWebToken(_, claimsSet, _) if jwtIsValid(value) => parseJwtToken(claimsSet)
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken(claimsSet: JwtClaimsSetJValue): Option[T] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      fromMap(claims).filter { auth =>
        val expiration = DateTime.now.plusSeconds(authExpirationTimeSeconds)
        auth.createdAt.isBefore(expiration)
      }
    }
  }

}

trait AuthDataAnonymousAuthFromFlowAuthHeader extends AuthDataFromFlowAuthHeader[AuthData.AnonymousAuth] {

  override protected def fromMap(data: Map[String, String]): Option[AuthData.AnonymousAuth] = {
    AuthData.AnonymousAuth.fromMap(data)
  }

}

trait AuthDataIdentifiedAuthFromFlowAuthHeader extends AuthDataFromFlowAuthHeader[AuthData.IdentifiedAuth] {

  override protected def fromMap(data: Map[String, String]): Option[AuthData.IdentifiedAuth] = {
    AuthData.IdentifiedAuth.fromMap(data)
  }

}

trait AuthDataIdentifiedOrgAuthFromFlowAuthHeader extends AuthDataFromFlowAuthHeader[AuthData.IdentifiedOrgAuth] {

  override protected def fromMap(data: Map[String, String]): Option[AuthData.IdentifiedOrgAuth] = {
    AuthData.IdentifiedOrgAuth.fromMap(data)
  }

}

trait AuthDataAnonymousOrgAuthFromFlowAuthHeader extends AuthDataFromFlowAuthHeader[AuthData.AnonymousOrgAuth] {

  override protected def fromMap(data: Map[String, String]): Option[AuthData.AnonymousOrgAuth] = {
    AuthData.AnonymousOrgAuth.fromMap(data)
  }

}