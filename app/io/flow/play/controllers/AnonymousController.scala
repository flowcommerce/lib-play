package io.flow.play.controllers

import io.flow.user.v0.models.User
import io.flow.play.clients.UserTokensClient
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
    * Needed to fetch users by token when a token is present in the
    * HTTP Request Headers.
    */
  def userTokensClient: UserTokensClient

  /**
   * Extracts the user from the headers
   */
  def getUser(headers: play.api.mvc.Headers)(
    implicit ec: ExecutionContext
  ): Future[Option[User]]

  class AnonymousRequest[A](
    val user: Future[Option[User]],
    request: Request[A]
  ) extends WrappedRequest[A](request)

  object Anonymous extends ActionBuilder[AnonymousRequest] {

    def invokeBlock[A](request: Request[A], block: (AnonymousRequest[A]) => Future[Result]) = {
      block(
        new AnonymousRequest(
          user = Headers(userTokensClient).user(request.headers),
          request = request
        )
      )
    }

  }

}

trait UserFromBasicAuthorizationToken {

  def userTokensClient: UserTokensClient

  def getUser(headers: play.api.mvc.Headers)(
    implicit ec: ExecutionContext
  ): Future[Option[User]] = {
    Headers(userTokensClient).user(headers)
  }

}

trait AnonymousRestController extends AnonymousController with UserFromBasicAuthorizationToken

