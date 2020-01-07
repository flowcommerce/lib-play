package io.flow.play.controllers

import com.github.ghik.silencer.silent
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import pdi.jwt.JwtAlgorithm.HS256
import pdi.jwt.JwtJson
import play.api.libs.json.JsObject
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{Headers, Request, Result}

import scala.util.Success

trait FlowActionInvokeBlockHelper {
  def config: Config

  @silent def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt: String = config.requiredString("JWT_SALT")

  protected val DefaultAuthExpirationTimeSeconds = 180

  protected lazy val authExpirationTimeSeconds: Int =
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)

  protected def auth[T <: AuthData](headers: Headers)(f: Map[String, String] => Option[T]): Option[T] =
    headers.get(AuthHeaders.Header).flatMap { v => parse(v)(f) }

  def parse[T <: AuthData](value: String)(f: Map[String, String] => Option[T]): Option[T] =
    JwtJson.decodeJson(value, jwtSalt, Seq(HS256)) match {
      case Success(claims) => parseJwtToken(claims)(f)
      case _ => None
    }

  protected def parseJwtToken[T <: AuthData](claims: JsObject)(f: Map[String, String] => Option[T]): Option[T] =
    claims.asOpt[Map[String, String]].flatMap { claims =>
      f(claims).filter(_.createdAt.plusSeconds(authExpirationTimeSeconds).isAfterNow)
    }
}
