package io.flow.play.clients

import io.flow.common.v0.models.{Audit, Reference}
import io.flow.user.v0.models.{Name, NameForm, User, UserForm}
import java.util.UUID
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

  def getUserByGuid(
    guid: String
  )(implicit ec: ExecutionContext): Future[Option[User]] = {
    MockUserClient.getUserByGuid(guid)
  }

}


object MockUserClient {

  private var usersByGuid = scala.collection.mutable.Map[UUID, User]()
  private var usersByToken = scala.collection.mutable.Map[String, User]()

  def makeAudit(): Audit = {
    val reference = Reference(UUID.randomUUID())
    val now = new DateTime()
    Audit(
      createdAt = now,
      createdBy = reference,
      updatedAt = now,
      updatedBy = reference
    )
  }

  /**
    * Constructs a user object - in memory only.
    */
  def makeUser(
    form: UserForm = makeUserForm()
  ): User = {
    User(
      guid = UUID.randomUUID,
      email = form.email,
      name = form.name match {
        case None => Name()
        case Some(n) => Name(
          first = n.first,
          last = n.first
        )
      },
      audit = makeAudit
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
