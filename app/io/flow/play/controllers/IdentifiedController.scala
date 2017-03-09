package io.flow.play.controllers

import io.flow.common.v0.models.UserReference
import io.flow.play.util.AuthData
import play.api.mvc.Results.Unauthorized

import scala.concurrent.Future
import play.api.mvc._

/**
  * Provides helpers for actions that require a user to be identified.
  */
trait IdentifiedController extends FlowControllerHelpers with AuthDataIdentifiedAuthFromFlowAuthHeader {

  import scala.concurrent.ExecutionContext.Implicits.global

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class IdentifiedRequest[A](
    val auth: AuthData.IdentifiedAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: UserReference = auth.user
  }

  object Identified extends ActionBuilder[IdentifiedRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers) match {
        case None => Future(
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new IdentifiedRequest(ad, request)
          )
        }
      }
    }
  }
}

trait IdentifiedRestController extends IdentifiedController with UserFromFlowAuth
