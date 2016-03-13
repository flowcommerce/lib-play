package io.flow.play.clients

import io.flow.common.v0.models.UserReference
import io.flow.token.v0.Client
import io.flow.token.v0.errors.UnitResponse
import scala.concurrent.{ExecutionContext, Future}

trait TokenClient {

  def getByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]]

}

@javax.inject.Singleton
class DefaultTokenClient @javax.inject.Inject() (registry: Registry) extends TokenClient {

  private[this] lazy val client: Client = new Client(registry.host("token"))

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
    * Getch the given token from the token service, and if found,
    * return the underlying user reference.
    */
  def getByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    callWith404 {
      client.tokens.getByToken(token).map(_.user)
    }
  }

}
