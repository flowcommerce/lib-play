package io.flow.play.controllers

import io.flow.log.RollbarLogger
import io.flow.play.util.Config
import javax.inject.Inject
import pdi.jwt.{Jwt, JwtAlgorithm, JwtJson, JwtOptions}

import scala.util.{Failure, Success}

sealed trait Authorization

case class JwtToken(userId: String) extends Authorization
case class Token(token: String) extends Authorization

class AuthorizationImpl @Inject() (
  logger: RollbarLogger,
  config: Config,
) {

  private[this] lazy val jwtSalt = {
    config.requiredString("JWT_SALT")
  }

  def get(value: Option[String]): Option[Authorization] = {
    value.flatMap { get }
  }

  /** Parses the actual authorization header value. Acceptable types are:
    *   - Basic - the API Token for the user.
    *   - Bearer - the JWT Token for the user with that contains an id field representing the user id in the database
    */
  def get(headerValue: String): Option[Authorization] = {
    headerValue.split(" ").toList match {
      case "Basic" :: value :: Nil =>
        new String(java.util.Base64.getDecoder.decode(value.getBytes)).split(":").toList match {
          case Nil => None
          case token :: _ => Some(Token(token))
        }

      case "Bearer" :: value :: Nil =>
        // allowlist only hmac algorithms
        JwtJson.decodeJson(value, jwtSalt, JwtAlgorithm.allHmac()) match {
          case Success(claims) =>
            (claims \ "id").asOpt[String].map(JwtToken)
          case Failure(ex) =>
            if (Jwt.isValid(value, JwtOptions.DEFAULT.copy(signature = false)))
              logger.info("JWT Token was valid, but we can't verify the signature", ex)
            None
        }

      case _ => None
    }
  }

}
