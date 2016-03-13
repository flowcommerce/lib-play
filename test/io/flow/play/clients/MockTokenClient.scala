package clients

import io.flow.play.clients
import io.flow.play.util.IdGenerator
import io.flow.common.v0.models.UserReference
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

trait MockTokenClient {

  val idGenerator = IdGenerator("test")

  lazy val testUser = makeUserReference()

  def createTestToken(): String = {
    "test"
  }

  def makeUserReference() = {
    UserReference(id = idGenerator.randomId())
  }

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)

  val port = 8002

  lazy val anonClient = new clients.MockTokenClient()
  lazy val identifiedClient = makeIdentifiedClient(user = UserReference(id = "usr-test-123"))

  /**
    * Generates an instance of the client where the user has been
    * granted all privileges.
    */
  def makeIdentifiedClient(
    user: UserReference = makeUserReference(),
    token: Option[String] = None
  ): clients.TokenClient = {
    token.map { clients.MockTokenClient.add(_, user)}
    new clients.MockTokenClient()
  }
}
