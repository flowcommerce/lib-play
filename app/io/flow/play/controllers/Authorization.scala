package io.flow.play.controllers

import javax.inject.Inject
import io.flow.play.util.Config
import org.apache.commons.codec.binary.Base64
import authentikat.jwt._
import io.flow.log.RollbarLogger

import scala.util.Try

trait Authorization

case class JwtCustomerToken(customer: String) extends Authorization
case class JwtToken(userId: String) extends Authorization
case class Token(token: String) extends Authorization

class AuthorizationImpl @Inject() (
  logger: RollbarLogger,
  config: Config
) {

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

            val data = tokenData match {
              case Some(t) =>
                t match {
                  case userToken: JwtToken => userToken.userId
                  case customerToken: JwtCustomerToken => customerToken.customer
                  case _ => "unknown"
                }

              case _ => "unknown"

            }

            logger.
              withKeyValue("data", data).
              info("JWT Token was invalid, bad salt")
            None
          case _ => None
        }
      }
      case _ => None
    }
  }

  private[this] def jwtIsValid(token: String): Boolean =
    // swallow errors when decoding - for instance algo not supported
    Try(JsonWebToken.validate(token, jwtSalt)).getOrElse(false)

  private[this] def createJwtToken(claimsSet: JwtClaimsSetJValue): Option[Authorization] =
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      val userIdToken = claims.get("id").map(JwtToken)
      val customerToken = claims.get("customer").map(JwtCustomerToken)

      userIdToken.orElse(customerToken)
    }
}
