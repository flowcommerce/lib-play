package io.flow.play.controllers

import akka.util.ByteString
import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.api.mocker.v0.models.MockApiResponse
import io.flow.api.mocker.v0.models.json._
import io.flow.play.util.{AuthData, AuthHeaders, Config}
import play.api.libs.json.Json
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{Headers, Request, ResponseHeader, Result}

trait FlowActionInvokeBlockHelper {
  def config: Config

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  def jwtSalt: String = config.requiredString("JWT_SALT")

  val `X-Flow-Mock-Api` = "X-Flow-Mock-Api"
  val `X-Flow-Mock-Api-Secret` = "X-Flow-Mock-Api-Secret"
  def mockApiSecret: String = config.requiredString("MOCK_API_SECRET")

  protected val DefaultAuthExpirationTimeSeconds = 180

  protected lazy val authExpirationTimeSeconds: Int =
    config.optionalPositiveInt("FLOW_AUTH_EXPIRATION_SECONDS").getOrElse(DefaultAuthExpirationTimeSeconds)

  protected def mockApi(headers: Headers): Option[Result] =
    for {
      secretFromHeader <- headers.get(`X-Flow-Mock-Api-Secret`)
      canMock = mockApiSecret == secretFromHeader
      mockApiResponse <- if (canMock) headers.get(`X-Flow-Mock-Api`).map(Json.parse(_).as[MockApiResponse]) else None
      result <- Option(Result(
        ResponseHeader(status = mockApiResponse.httpStatusCode),
        play.api.http.HttpEntity.Strict(ByteString(mockApiResponse.body.toString),Option(mockApiResponse.contentType))
      ))
    } yield result

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