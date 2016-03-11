package io.flow.play.clients

import io.flow.play.util.IdGenerator
import io.flow.common.v0.models.UserReference
import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class MockUserTokensClient extends UserTokensClient {

  def add(
    user: UserReference,
    token: Option[String] = None
  ) {
    MockUserTokensClient.add(user, token)
  }

  override def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    MockUserTokensClient.getUserByToken(token)
  }

}


object MockUserTokensClient {

  private[this] var usersById = scala.collection.mutable.Map[String, UserReference]()
  private[this] var usersByToken = scala.collection.mutable.Map[String, UserReference]()
  private[this] val idGenerator = IdGenerator("tst")

  /**
    * Constructs a user object - in memory only.
    */
  def makeUserReference() = UserReference(id = idGenerator.randomId())

  def add(
    user: UserReference,
    token: Option[String] = None
  ) {
    usersById ++= Map(user.id -> user)
    token.map { value =>
      usersByToken ++= Map(value -> user)
    }
  }

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[UserReference]] = {
    Future { usersByToken.get(token) }
  }

}
