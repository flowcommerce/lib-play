package io.flow.play.controllers

import javax.inject.Inject

import io.flow.play.util.Config
import org.apache.commons.codec.binary.Base64
import authentikat.jwt._
import play.api.{Application, Logger}

trait Authorization

case class JwtToken(userId: String) extends Authorization
case class Token(token: String) extends Authorization

class AuthorizationImpl @Inject() (config: Config) {

  private[this] lazy val jwtSalt = {
    config.requiredString("JWT_SALT")
  }

  def get(value: Option[String]): Option[Authorization] = {
    value.flatMap { get }
  }

  /**
    * Parses the actual authorization header value. Acceptable types are:
    * - Basic - the API Token for the user.
    * - Bearer - the JWT Token for the user with that contains an id field representing the user id in the database
    */
  def get(headerValue: String): Option[Authorization] = {
    headerValue.split(" ").toList match {
      case "Basic" :: value :: Nil => {
        new String(Base64.decodeBase64(value.getBytes)).split(":").toList match {
          case Nil => None
          case token :: _ => Some(Token(token))
        }
      }

      case "Bearer" :: value :: Nil => {
        value match {
          case JsonWebToken(_, claimsSet, _) if jwtIsValid(value) => createJwtToken(claimsSet)
          case JsonWebToken(_, claimsSet, _) =>
            val tokenData = createJwtToken(claimsSet)
            Logger.warn(s"JWT Token for user[${tokenData.map(_.userId).getOrElse("unknown")}] was invalid, bad salt")
            None
          case _ => None
        }
      }
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def createJwtToken(claimsSet: JwtClaimsSetJValue): Option[JwtToken] =
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      claims.get("id").map(JwtToken)
    }
}
