package io.flow.play.clients

import io.flow.common.v0.models.UserReference
import io.flow.user.v0.Client
import io.flow.user.v0.errors.UnitResponse
import scala.concurrent.{ExecutionContext, Future}

object UserClient {

  val SystemUser = UserReference("usr-20151006-1")
  val AnonymousUser = UserReference("usr-20151006-2")

}

trait UserTokensClient {

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]]

}

@javax.inject.Singleton
class DefaultUserTokensClient @javax.inject.Inject() (registry: Registry) extends UserTokensClient {

  lazy val client: Client = new Client(registry.host("user"))

  def callWith404[T](
    f: Future[T]
  )(implicit ec: ExecutionContext): Future[Option[T]] = {
    f.map {
      value => Some(value)
    }.recover {
      case UnitResponse(404) => None
      case ex: Throwable => throw ex
    }
  }

  /**
    * Fetch a user by API Token.
    */
  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    callWith404( client.users.getTokensByToken(token).map { u => UserReference(id = u.id) } )
  }

}
