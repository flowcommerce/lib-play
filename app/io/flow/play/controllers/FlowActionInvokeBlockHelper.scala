package io.flow.play.controllers

import io.flow.play.jwt.JwtService
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import play.api.mvc.{Headers, Request, Result}
import play.api.mvc.Results.Unauthorized

trait FlowActionInvokeBlockHelper {
  def config: Config

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtService: JwtService

  protected val DefaultAuthExpirationTimeSeconds = 180

  protected lazy val authExpirationTimeSeconds: Int =
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)

  protected def auth[T <: AuthData](headers: Headers)(f: Map[String, String] => Option[T]): Option[T] =
    headers.get(AuthHeaders.Header).flatMap { v => parse(v)(f) }

  def parse[T <: AuthData](value: String)(f: Map[String, String] => Option[T]): Option[T] =
    jwtService
      .decode(value)
      .toOption
      .flatMap(f)
      .filter(_.createdAt.plusSeconds(authExpirationTimeSeconds).isAfterNow)

}