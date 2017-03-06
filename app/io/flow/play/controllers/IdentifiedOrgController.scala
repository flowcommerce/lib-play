package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, UserReference}
import io.flow.play.util.{AuthData, OrganizationAuthData}
import play.api.mvc.Results.Unauthorized

import scala.concurrent.Future
import play.api.mvc._

/**
  * Provides helpers for actions that require a user and an
  * organization to be identified.
  */
trait IdentifiedOrgController extends AnonymousController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class IdentifiedOrgRequest[A](
    val auth: AuthData,
    val orgAuth: OrganizationAuthData,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: UserReference = auth.user
    val organization: String = orgAuth.organization
    val environment: Environment = orgAuth.environment
  }

  object IdentifiedOrg extends ActionBuilder[IdentifiedOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers) match {
        case None => Future(
          unauthorized(request)
        )
        case Some(auth) => {
          auth.organization match {
            case None => Future (
              unauthorized(request)
            )
            case Some(org) => {
              block(
                new IdentifiedOrgRequest(auth, org, request)
              )
            }
          }
        }
      }
    }
  }
}
