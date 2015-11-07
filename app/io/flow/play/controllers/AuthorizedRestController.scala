package io.flow.play.controllers

import io.flow.user.v0.models.User
import io.flow.authorization.v0.models.Check
import io.flow.play.clients.AuthorizationClient
import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results.Unauthorized
import scala.concurrent.Future

trait AuthorizedRestController extends AnonymousRestController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def authorizationClient: AuthorizationClient

  class AuthenticatedRequest[A](
    val user: User,
    request: Request[A]
  ) extends WrappedRequest[A](request)

  case class Authenticated(
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  ) extends ActionBuilder[AuthenticatedRequest] {

    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
      Headers(userTokensClient).user(request.headers).flatMap { userOption =>
        userOption match {
          case None => {
            Future { Unauthorized }
          }
          case Some(user) => {
            authorizationsClient.authorize(
              userGuid = user.guid,
              creates = creates,
              reads = reads,
              updates = updates,
              deletes = deletes
            ).flatMap { result =>
              result match {
                case Check(true, reason) => {
                  block(
                    new AuthenticatedRequest(user, request)
                  )
                }
                case Check(false, reason) => {
                  val contexts = Seq(
                    creates.map { context => s"creates($context)" },
                    reads.map { context => s"reads($context)" },
                    updates.map { context => s"updates($context)" },
                    deletes.map { context => s"deletes($context)" }
                  ).flatten.mkString(" ")
                  Logger.info(s"authorization denied for userGuid[${user.guid}] context[$contexts]: $reason")
                  Future {
                    Unauthorized
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
