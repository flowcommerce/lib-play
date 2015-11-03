package io.flow.play.controllers

import org.apache.commons.codec.binary.Base64

object BasicAuthorization {

  trait Authorization
  case class Token(token: String) extends Authorization

  def get(value: Option[String]): Option[Authorization] = {
    value.flatMap { get(_) }
  }

  /**
   * Parses the actual authorization header value
   */
  def get(value: String): Option[Authorization] = {
    value.split(" ").toList match {
      case "Basic" :: value :: Nil => {
        new String(Base64.decodeBase64(value.getBytes)).split(":").toList match {
          case Nil => None
          case token :: rest => Some(Token(token))
        }
      }
      case _ => None
    }
  }

}
