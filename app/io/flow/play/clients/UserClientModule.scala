package io.flow.play.clients

import com.google.inject.{AbstractModule, Provides, Singleton}
import io.flow.common.v0.models.Name
import play.api.{Environment, Configuration, Mode}

class UserClientModule(
  env: Environment,
  config: Configuration
  ) extends AbstractModule {

  private[this] val userApiHost = config.getString("user.api.host").getOrElse("Missing user.api.host")

  override def configure() {}

  @Singleton @Provides
  def provideUserClient(): io.flow.user.v0.interfaces.Client = {
    env.mode match {
      case Mode.Prod | Mode.Dev => new io.flow.user.v0.Client(userApiHost)
      case Mode.Test => new MockUserClient()
    }
  }

}

class MockUserClient extends io.flow.user.v0.mock.Client {

  import io.flow.common.v0.models.User

  override def users = Mock()

  case class Mock() extends io.flow.user.v0.mock.MockUsers {

    private[this] val users = scala.collection.mutable.ListBuffer[User](User(id = "usr-123", email = Some("test@flow.io"), name = Name(Some("testFirst"),Some("testLast"))))
    private[this] var usersByToken = scala.collection.mutable.Map[String, User]()

    override def get(
      id: _root_.scala.Option[Seq[String]] = None,
      email: _root_.scala.Option[String] = None,
      token: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at"
    )(implicit ec: scala.concurrent.ExecutionContext) = scala.concurrent.Future {
      users.slice(offset.toInt, limit.toInt)
    }
  }
}
