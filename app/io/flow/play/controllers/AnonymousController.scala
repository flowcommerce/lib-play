package io.flow.play.controllers

import io.flow.play.clients.DefaultTokenClient
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
trait AnonymousController extends FlowControllerHelpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * token client is used to validate API tokens when present in
    * requests
    */
  def tokenClient: TokenClient

  /**
   * Extracts the user from the headers
   */
  def user(
    session: Session,
    headers: Headers,
    path: String,
    queryString: Map[String, Seq[String]]
  ) (
    implicit ec: ExecutionContext
  ): Future[Option[UserReference]]

  class AnonymousRequest[A](
    val user: Future[Option[UserReference]],
    request: Request[A]
  ) extends WrappedRequest[A](request)

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]) = {
      block(
        new AnonymousRequest(
          user = user(request.session, request.headers, request.path, request.queryString),
          request = request
        )
      )
    }

  }

}

trait AnonymousRestController extends AnonymousController with UserFromFlowAuth

