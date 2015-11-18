package io.flow.play.clients

import java.util.UUID
import io.flow.user.v0.models.User
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class MockUserClient extends UserTokensClient {

  def add(
    user: User,
    token: Option[String] = None
  ) {
    MockUserClient.add(user, token)
  }

  override def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    MockUserClient.getUserByToken(token)
  }

  def getUserByGuid(
    guid: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    MockUserClient.getUserByGuid(guid)
  }

}


object MockUserClient {

  private var usersByGuid = scala.collection.mutable.Map[UUID, User]()
  private var usersByToken = scala.collection.mutable.Map[String, User]()

  def add(
    user: User,
    token: Option[String] = None
  ) {
    usersByGuid ++= Map(user.guid -> user)
    token.map { value =>
      usersByToken ++= Map(value -> user)
    }
  }

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    Future { usersByToken.get(token) }
  }

  def getUserByGuid(
    guid: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    Future {
      Try(UUID.fromString(guid)) match {
        case Success(validGuid) => usersByGuid.get(validGuid)
        case Failure(_) => None
      }
    }
  }

}
