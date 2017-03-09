package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, UserReference}
import io.flow.play.util.AuthData
import io.flow.play.util.OrgData.AnonymousOrgData
import play.api.mvc.Results.Unauthorized
import play.api.mvc._

import scala.concurrent.Future

/**
  * Provides helpers for actions that require an organization
  * but allow for a user to be anonymous. Common example is a
  * user creating an order from a session on shopify.
  */
trait AnonymousOrgController extends FlowControllerHelpers with AuthDataAnonymousOrgAuthFromFlowAuthHeader {

  import scala.concurrent.ExecutionContext.Implicits.global

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class AnonymousOrgRequest[A](
    val auth: AuthData.AnonymousOrgAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: Option[UserReference] = auth.user
    val organization: String = auth.orgData.organization
    val environment: Environment = auth.orgData.environment
  }

  object AnonymousOrg extends ActionBuilder[AnonymousOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers) match {
        case None => Future (
          unauthorized(request)
        )
        case Some(ad) => {
          block(
            new AnonymousOrgRequest(ad, request)
          )
        }
      }
    }
  }
}

case class OrganizationAnonymousAuthData(
  organization: String,
  environment: Environment
)