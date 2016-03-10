package io.flow.play.clients

import io.flow.play.util.IdGenerator
import io.flow.common.v0.models.{Name, User}
import io.flow.user.v0.models.UserForm
import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class MockUserTokensClient extends UserTokensClient {

  def add(
    user: User,
    token: Option[String] = None
  ) {
    MockUserTokensClient.add(user, token)
  }

  override def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    MockUserTokensClient.getUserByToken(token)
  }

  override def getUserById(
    userId: String
  )(implicit  ec: ExecutionContext): Future[Option[User]] = {
    MockUserTokensClient.getUserById(userId)
  }

}


object MockUserTokensClient {

  private[this] var usersById = scala.collection.mutable.Map[String, User]()
  private[this] var usersByToken = scala.collection.mutable.Map[String, User]()
  private[this] val idGenerator = IdGenerator("tst")

  /**
    * Constructs a user object - in memory only.
    */
  def makeUser(
    form: UserForm = makeUserForm()
  ): User = {
    User(
      id = idGenerator.randomId(),
      email = form.email,
      name = form.name match {
        case None => Name()
        case Some(n) => Name(
          first = n.first,
          last = n.first
        )
      }
    )
  }

  def makeUserForm() = UserForm(
    email = None,
    name = None,
    avatarUrl = None
  )

  def add(
    user: User,
    token: Option[String] = None
  ) {
    usersById ++= Map(user.id -> user)
    token.map { value =>
      usersByToken ++= Map(value -> user)
    }
  }

  def getUserByToken(
    token: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    Future { usersByToken.get(token) }
  }

  def getUserById(
    userId: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    Future { usersById.get(userId) }
  }

}
