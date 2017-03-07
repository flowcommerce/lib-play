package io.flow.play.controllers

import io.flow.common.v0.models.UserReference
import io.flow.play.util.AuthData
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
    val auth: AuthData.IdentifiedAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: UserReference = auth.user
  }

  object Identified extends ActionBuilder[IdentifiedRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedRequest[A]) => Future[Result]): Future[Result] = {
      val authData = auth(request.headers).flatMap {
        case _: AuthData.AnonymousAuth => None
        case a: AuthData.IdentifiedAuth => Some(a)
        case a: AuthData.AnonymousOrgAuth => None
        case a: AuthData.IdentifiedOrgAuth => Some(
          AuthData.IdentifiedAuth(
            createdAt = a.createdAt,
            requestId = a.requestId,
            user = a.user
          )
        )
      }

      authData match {
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
