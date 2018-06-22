package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import play.api.mvc.{Headers, Request, Result}
import play.api.mvc.Results.Unauthorized

trait FlowActionInvokeBlockHelper {
  def config: Config

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt: String = config.requiredString("JWT_SALT")

  protected val DefaultAuthExpirationTimeSeconds = 180

  protected lazy val authExpirationTimeSeconds: Int =
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)

  protected def auth[T <: AuthData](headers: Headers)(f: Map[String, String] => Option[T]): Option[T] =
    headers.get(AuthHeaders.Header).flatMap { v => parse(v)(f) }

  def parse[T <: AuthData](value: String)(f: Map[String, String] => Option[T]): Option[T] =
    value match {
      case JsonWebToken(_, claimsSet, _) if jwtIsValid(value) => parseJwtToken(claimsSet)(f)
      case _ => None
  }

  protected def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  protected def parseJwtToken[T <: AuthData](claimsSet: JwtClaimsSetJValue)(f: Map[String, String] => Option[T]): Option[T] =
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
    f(claims).filter { auth => auth.createdAt.plusSeconds(authExpirationTimeSeconds).isAfterNow }
  }
}
