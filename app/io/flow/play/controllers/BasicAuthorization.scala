package io.flow.play.controllers

import io.flow.play.util.EnvironmentConfig
import org.apache.commons.codec.binary.Base64

import authentikat.jwt._
import play.api.Logger

object BasicAuthorization {

  trait Authorization
  case class Token(token: String) extends Authorization
  case class JWTToken(userId: String) extends Authorization

  val jwtSalt = EnvironmentConfig.requiredString("JWT_SALT")

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
          case token :: rest => Some(Token(token))
        }
      }

      case "Bearer" :: value :: Nil => {
        value match {
          case JsonWebToken(header, claimsSet, signature) if jwtIsValid(value) => createJWTToken(claimsSet)
          case JsonWebToken(header, claimsSet, signature) =>
            val tokenData = createJWTToken(claimsSet)
            Logger.error(s"JWT Token for user[${tokenData.map(_.userId).getOrElse("unknown")}] was invalid, bad salt")
            None
          case _ => None
        }
      }
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def createJWTToken(claimsSet: JwtClaimsSetJValue): Option[JWTToken] =
    claimsSet.asSimpleMap.toOption match {
      case Some(claims) => claims.get("id").map(JWTToken)
      case _ => None
    }
}
