package io.flow.play.controllers

import io.flow.play.util.AuthData
import io.flow.common.v0.models.UserReference
import io.flow.token.v0.interfaces.{Client => TokenClient}

import scala.concurrent.Future
import play.api.mvc._

/**
  * Primarily a marker to indicate intention to make all actions in a
  * controller anonymous. Also includes a few helper methods to interact
  * with users - intended to allow an anonymous action to succeed in cases
  * where we may or may not have a user.
  */
trait AnonymousController extends FlowControllerHelpers with AuthDataFromFlowAuthHeader {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * token client is used to validate API tokens when present in
    * requests
    */
  def tokenClient: TokenClient

  class AnonymousRequest[A](
    val auth: Option[AuthData.AnonymousAuth],
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: Option[UserReference] = auth.flatMap(_.user)
  }

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val authData = auth(request.headers).map {
        case a: AuthData.AnonymousAuth => a
        case a: AuthData.IdentifiedAuth => AuthData.AnonymousAuth(
          createdAt = a.createdAt,
          requestId = a.requestId,
          user = Some(a.user)
        )
        case a: AuthData.AnonymousOrgAuth => AuthData.AnonymousAuth(
          createdAt = a.createdAt,
          requestId = a.requestId,
          user = a.user
        )
        case a: AuthData.IdentifiedOrgAuth => AuthData.AnonymousAuth(
          createdAt = a.createdAt,
          requestId = a.requestId,
          user = Some(a.user)
        )
      }

      block(
        new AnonymousRequest(
          auth = authData,
          request = request
        )
      )
    }

  }

}

trait AnonymousRestController extends AnonymousController with UserFromFlowAuth

