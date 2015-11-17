package io.flow.play.controllers

import io.flow.user.v0.models.User
import play.api.mvc.Results.Unauthorized
import scala.concurrent.Future
import play.api.mvc._

/**
  * Provides helpers for actions that require a user to be identified.
  */
trait IdentifiedRestController extends AnonymousRestController {

  import scala.concurrent.ExecutionContext.Implicits.global

  class IdentifiedRequest[A](
    val user: User,
    request: Request[A]
  ) extends WrappedRequest[A](request)

  object Identified extends ActionBuilder[IdentifiedRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]) = {
      Headers(userTokensClient).user(request.headers).flatMap { userOption =>
        userOption match {
          case None => {
            Future { Unauthorized }
          }
          case Some(user) => {
            block(
              new IdentifiedRequest(user, request)
            )
          }
        }
      }
    }
  }

}

