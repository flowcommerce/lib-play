package io.flow.play.clients

import io.flow.common.v0.models.{User, UserReference}
import io.flow.user.v0.mock.{Client, MockUsers}
import scala.concurrent.{ExecutionContext, Future}

trait MockUserClient extends Factories {

  // val port = 6021

  object MockClientImpl extends Client {

    override def users = MockUsersImpl

  }

  object MockUsersImpl extends MockUsers {

    private[this] var users = scala.collection.mutable.ListBuffer[User]()

    def add(user: User) = {
      users += user
    }

    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      email: _root_.scala.Option[String] = None,
      token: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at"
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]] = scala.concurrent.Future {
      users.filter { u =>
        id match {
          case None => true
          case Some(ids) => ids.contains(u.id)
        }

      }.filter { u =>
        email match {
          case None => true
          case Some(email) => u.email == Some(email)
        }

      }.filter { u =>
        token match {
          case None => true
          case Some(token) => {
            MockTokenClient.getByToken(token) match {
              case None => false
              case Some(ref) => u.id == ref.id
            }
          }
        }
      }.drop(offset).take(limit)

    }
    

  }


  lazy val anonClient = MockClientImpl

  lazy val identifiedClient = makeIdentifiedClient(
    user = UserReference(id = "usr-test-123"),
    token = makeToken()
  )

  /**
    * Generates an instance of the client where the user has been
    * granted all privileges.
    */
  def makeIdentifiedClient(
    user: UserReference = makeUserReference(),
    token: Option[String] = None
  ): Client = {
    token.map { clients.MockUserClient.add(_, user)}

    new Client {

    }
  }
}
