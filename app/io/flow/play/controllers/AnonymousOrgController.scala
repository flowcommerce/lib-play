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
trait AnonymousOrgController extends AnonymousController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class AnonymousOrgRequest[A](
    val auth: AuthData.AnonymousOrgAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: Option[UserReference] = auth.user
    val organization: String = auth.organization.organization
    val environment: Environment = auth.organization.environment
  }

  object AnonymousOrg extends ActionBuilder[AnonymousOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousOrgRequest[A]) => Future[Result]): Future[Result] = {
      val authData = auth(request.headers).flatMap {
        case _: AuthData.AnonymousAuth => None
        case _: AuthData.IdentifiedAuth => None
        case a: AuthData.AnonymousOrgAuth => Some(a)
        case a: AuthData.IdentifiedOrgAuth => Some(
          AuthData.AnonymousOrgAuth(
            createdAt = a.createdAt,
            requestId = a.requestId,
            user = Some(a.user),
            organization = AnonymousOrgData(
              organization = a.orgData.organization,
              environment = a.orgData.environment
            )
         )
        )
      }

      authData match {
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