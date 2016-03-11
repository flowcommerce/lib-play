package io.flow.play.controllers

import io.flow.common.v0.models.{UserReference, User}
import play.api.mvc.Results.Unauthorized
import scala.concurrent.Future
import play.api.mvc._

/**
  * Provides helpers for actions that require a user to be identified.
  */
trait IdentifiedController extends AnonymousController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class IdentifiedRequest[A](
    val user: UserReference,
    request: Request[A]
  ) extends WrappedRequest[A](request)

  object Identified extends ActionBuilder[IdentifiedRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]) = {
      user(request.session, request.headers, request.path, request.queryString).flatMap {
        case None => {
          Future { unauthorized(request) }
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

trait IdentifiedRestController extends IdentifiedController with UserFromAuthorizationToken
