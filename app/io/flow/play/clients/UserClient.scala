package io.flow.play.clients

import io.flow.play.util.DefaultConfig

import io.flow.user.v0.Client
import io.flow.user.v0.errors.UnitResponse
import io.flow.common.v0.models.{User, UserReference}
import scala.concurrent.{ExecutionContext, Future}

object UserClient {

  val SystemUser = UserReference("usr-20151006-1")
  val AnonymousUser = UserReference("usr-20151006-2")

}

trait UserTokensClient {

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]]

}

@javax.inject.Singleton
class DefaultUserTokensClient() extends UserTokensClient {

  // e.g. http://api.flow.io/users
  def host: String = DefaultConfig.requiredString("user.api.host")

  lazy val client = new Client(host)

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
    * Blocking call to fetch a user by API Token.
    */
  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    callWith404( client.users.getTokensByToken(token) )
  }

}
