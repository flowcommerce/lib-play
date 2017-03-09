package io.flow.play.controllers

import io.flow.common.v0.models.{Environment, UserReference}
import io.flow.play.util.AuthData
import play.api.mvc.Results.Unauthorized

import scala.concurrent.Future
import play.api.mvc._

/**
  * Provides helpers for actions that require a user and an
  * organization to be identified.
  */
trait IdentifiedOrgController extends FlowControllerHelpers with AuthDataFromFlowAuthHeader[AuthData.IdentifiedOrgAuth] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override protected def fromMap(data: Map[String, String]): Option[AuthData.IdentifiedOrgAuth] = {
    AuthData.IdentifiedOrgAuth.fromMap(data)
  }

  def unauthorized[A](request: Request[A]): Result = Unauthorized

  class IdentifiedOrgRequest[A](
    val auth: AuthData.IdentifiedOrgAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: UserReference = auth.user
    val organization: String = auth.orgData.organization
    val environment: Environment = auth.orgData.environment
  }

  object IdentifiedOrg extends ActionBuilder[IdentifiedOrgRequest] {

    def invokeBlock[A](request: Request[A], block: (IdentifiedOrgRequest[A]) => Future[Result]): Future[Result] = {
      auth(request.headers) match {
        case None => Future.successful(
          unauthorized(request)
        )

        case Some(ad) => {
          block(
            new IdentifiedOrgRequest(ad, request)
          )
        }
      }
    }
  }
}
