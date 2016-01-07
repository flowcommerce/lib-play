package io.flow.play.controllers

import io.flow.play.clients.UserTokensClient
import io.flow.common.v0.models.User
import scala.concurrent.Future

object Headers {

  val Authorization = "Authorization"

  /**
    * If present, parses the basic authorization header and returns
    * its decoded value.
    */
  def basicAuthorizationToken(
    headers: play.api.mvc.Headers
  ): Option[String] = {
    headers.get(Authorization).flatMap { h =>
      BasicAuthorization.get(h) match {
        case Some(auth: BasicAuthorization.Token) => Some(auth.token)
        case _ => None
      }
    }
  }

}
