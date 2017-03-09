package io.flow.play.controllers

import io.flow.play.util.{AuthData, AuthHeaders}
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
trait AnonymousController extends FlowControllerHelpers with AuthDataAnonymousAuthFromFlowAuthHeader {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * token client is used to validate API tokens when present in
    * requests
    */
  def tokenClient: TokenClient

  class AnonymousRequest[A](
    val auth: AuthData.AnonymousAuth,
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: Option[UserReference] = auth.user
  }

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]): Future[Result] = {
      val ad = auth(request.headers).getOrElse {
        AuthData.AnonymousAuth(
          requestId = AuthHeaders.generateRequestId("anonymousrequest"),
          user = None
        )
      }

      block(
        new AnonymousRequest(ad, request)
      )
    }

  }

}