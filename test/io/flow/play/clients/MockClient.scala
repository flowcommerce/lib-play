package clients


import io.flow.user.v0.Client
import io.flow.play.clients._
import io.flow.common.v0.models.User
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

trait MockClient {

  lazy val testUser = MockUserTokensClient.makeUser()

  def createTestToken(): String = {
    "test"
  }

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)

  val port = 8002

  lazy val anonClient = new Client(s"http://localhost:$port")
  lazy val identifiedClient = makeIdentifiedClient(user = testUser.copy(id = "usr-123"))

  /**
    * Generates an instance of the client where the user has been
    * granted all privileges.
    */
  def makeIdentifiedClient(
    user: User = MockUserTokensClient.makeUser(),
    token: String = createTestToken()
  ): io.flow.user.v0.mock.Client = {
    MockAuthorizationClient.grantAll(user.id)
    MockUserTokensClient.add(user, token = Some(token))
    new MockUserClient()
  }
}
