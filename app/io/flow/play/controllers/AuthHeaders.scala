package io.flow.play.controllers

import io.flow.play.clients.UserTokenClient
import io.flow.user.v0.models.User
import scala.concurrent.Future

object AuthHeaders {

  val AuthorizationHeader = "Authorization"

}

case class AuthHeaders(users: UserTokenClient) {

  def user(
    headers: play.api.mvc.Headers
  )(
    implicit ec: scala.concurrent.ExecutionContext
  ): Future[Option[User]] = {
    headers.get(AuthHeaders.AuthorizationHeader) match {
      case None => Future { None }
      case Some(h) => {
        BasicAuthorization.get(h) match {
          case Some(auth: BasicAuthorization.Token) => {
            users.getUserByToken(auth.token)
          }
          case _ => {
            Future { None }
          }
        }
      }
    }
  }

}
