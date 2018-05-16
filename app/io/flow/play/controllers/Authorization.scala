package io.flow.play.controllers

import io.flow.play.jwt.JwtService
import io.flow.play.util.Config
import javax.inject.{Inject, Singleton}
import org.apache.commons.codec.binary.Base64

trait Authorization

case class JwtToken(userId: String) extends Authorization
case class Token(token: String) extends Authorization

@Singleton
class AuthorizationImpl @Inject() (config: Config, jwtService: JwtService) {

  def get(value: Option[String]): Option[Authorization] = value.flatMap(get)

  /**
    * Parses the actual authorization header value. Acceptable types are:
    * - Basic - the API Token for the user.
    * - Bearer - the JWT Token for the user with that contains an id field representing the user id in the database
    */
  def get(headerValue: String): Option[Authorization] = {
    headerValue.split(" ").toList match {
      case "Basic" :: value :: Nil =>
        new String(Base64.decodeBase64(value.getBytes)).split(":").headOption.map(Token)

      case "Bearer" :: value :: Nil =>
        jwtService.decode(value).toOption.flatMap(_.get("id").map(JwtToken))

      case _ => None
    }
  }

}
