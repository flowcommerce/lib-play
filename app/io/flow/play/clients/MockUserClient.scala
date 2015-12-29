package io.flow.play.clients

import io.flow.play.util.IdGenerator
import io.flow.user.v0.models.{Name, NameForm, User, UserForm}
import org.joda.time.DateTime
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

}


object MockUserClient {

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

}
