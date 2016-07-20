package io.flow.play.controllers

import io.flow.play.clients.DefaultTokenClient
import io.flow.play.util.AuthData
import io.flow.common.v0.models.UserReference
import io.flow.token.v0.interfaces.{Client => TokenClient}
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc._

/**
  * Primarily a marker to indicate intention to make all actions in a
  * controller anonymous. Also includes a few helper methods to interact
  * with users - intented to allow an anonymous action to succeed in cases
  * where we may or may not have a user.
  */
trait AnonymousController extends FlowControllerHelpers with AuthDataFromFlowAuthHeader {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * token client is used to validate API tokens when present in
    * requests
    */
  def tokenClient: TokenClient

  def auth(
    headers: Headers
  ) (
    implicit ec: ExecutionContext
  ): Option[AuthData]

  class AnonymousRequest[A](
    val auth: Option[AuthData],
    request: Request[A]
  ) extends WrappedRequest[A](request) {
    val user: Option[UserReference] = auth.map(_.user)
  }

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]) = {
      block(
        new AnonymousRequest(
          auth = auth(request.headers),
          request = request
        )
      )
    }

  }

}

trait AnonymousRestController extends AnonymousController with UserFromFlowAuth

