package io.flow.play.clients

import io.flow.play.util.DefaultConfig

import io.flow.user.v0.{Authorization, Client}
import io.flow.user.v0.errors.UnitResponse
import io.flow.user.v0.models.User
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
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

  def awaitCallWith404[T](
    future: Future[T]
  )(implicit ec: ExecutionContext): Option[T] = {
    Await.result(
      callWith404(future),
      1000.millis
    )
  }

  /**
    * Blocking call to fetch a user by Id.
    */
  def getUserById(
    id: String
  )(implicit ec: ExecutionContext): Option[User] = {
    awaitCallWith404( client.users.getById(id) )
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
