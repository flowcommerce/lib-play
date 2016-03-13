package io.flow.play.clients

import io.flow.play.util.IdGenerator
import io.flow.common.v0.models.UserReference
import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class MockTokenClient extends TokenClient {

  def add(token: String, user: UserReference) {
    MockTokenClient.add(token, user)
  }

  override def getByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    MockTokenClient.getByToken(token)
  }

}


object MockTokenClient {

  private[this] var usersByToken = scala.collection.mutable.Map[String, UserReference]()

  def add(token: String, user: UserReference) {
    usersByToken ++= Map(token -> user)
  }

  def getByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    Future { usersByToken.get(token) }
  }

}
