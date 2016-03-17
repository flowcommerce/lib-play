package io.flow.play.clients

import io.flow.common.v0.models.User
import io.flow.user.v0.mock
import scala.concurrent.{ExecutionContext, Future}

/**
  * This object emulates storage of the users for the mock user
  * client
  */
case class MockUserData() {

  var users = scala.collection.mutable.ListBuffer[User]()

  def add(user: User) = {
    users += user
  }

}

@javax.inject.Singleton
class MockUserClient @javax.inject.Inject() () extends mock.Client {

  val data = MockUserData()

  override def users = MockUsers(data)

}

case class MockUsers(data: MockUserData) extends mock.MockUsers {

  override def get(
    id: _root_.scala.Option[Seq[String]] = None,
    email: _root_.scala.Option[String] = None,
    limit: Long = 25,
    offset: Long = 0,
    sort: String = "-created_at"
  )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]] = scala.concurrent.Future {
    data.users.filter { u =>
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
      // token is deprecated - ignore for now as will shortly be removed
      true
    }.drop(offset.toInt).take(limit.toInt) // TODO: Add sorting
  }

  override def getById(
    id: String
  )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
    get(id = Some(Seq(id))).map { _.headOption.getOrElse {
      throw new io.flow.token.v0.errors.UnitResponse(404)
    }}
  }

}

