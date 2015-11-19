package io.flow.play.controllers

import io.flow.play.clients.UserTokensClient
import io.flow.user.v0.models.User
import scala.concurrent.Future

object Headers {

  val Authorization = "Authorization"

}

case class Headers(users: UserTokensClient) {

  /**
    * If present, parses the basic authorization header and returns
    * its decoded value.
    */
  def basicAuthorizationToken(
    headers: play.api.mvc.Headers
  ): Option[String] = {
    headers.get(Headers.Authorization).flatMap { h =>
      BasicAuthorization.get(h) match {
        case Some(auth: BasicAuthorization.Token) => Some(auth.token)
        case _ => None
      }
    }
  }

  def user(
    headers: play.api.mvc.Headers
  ) (
    implicit ec: scala.concurrent.ExecutionContext
  ): Future[Option[User]] = {
    basicAuthorizationToken(headers) match {
      case None => Future { None }
      case Some(token) => {
        users.getUserByToken(token)
      }
    }
  }

}
