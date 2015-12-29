package io.flow.play.clients

import io.flow.play.util.DefaultConfig

import io.flow.user.v0.{Authorization, Client}
import io.flow.user.v0.errors.UnitResponse
import io.flow.user.v0.models.User
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import java.util.UUID

object UserClient {

  val AnonymousUserGuid = UUID.fromString("f2374f80-3a59-4194-aed2-ef228e6171e3")

}

trait UserTokensClient {

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]]

}

@javax.inject.Singleton
class DefaultUserTokensClient() extends UserTokensClient {

  def host: String = DefaultConfig.requiredString("user.api.host")

  // host: e.g. http://api.flow.io/users
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
