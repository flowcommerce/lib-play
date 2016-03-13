package io.flow.play.expanders

import io.flow.play.clients.MockUserClient
import org.scalatestplus.play._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class UserSpec extends PlaySpec {
  import scala.concurrent.ExecutionContext.Implicits.global

  val client = play.api.Play.current.injector.instanceOf[UserClient]

  //For reference, an example of a GuiceApplicationBuilder
  //val application: Application = new GuiceApplicationBuilder()
    //.bindings(bind[AuthorizationClient].to[MockAuthorizationClient])
    //.bindings(bind[UserTokensClient].to[MockUserTokensClient])
    //.bindings(bind[io.flow.user.v0.mock.Client].to[MockUserClient])
    //.build

  val validExpandRecord = Seq(
    Json.parse(
    """
      {
        "user": {
          "id": "usr-123",
          "email": "test@flow.io",
          "name": {
           "first": "testFirst",
           "last": "testLast"
          },
          "discriminator": "user"
        }
      }
    """.stripMargin
    )
  )

  val userReferenceRecord =
    Json.parse(
      """
        {
          "user": {
            "id": "usr-bogus",
            "discriminator": "user_reference"
          }
        }
      """.stripMargin)


  val userReferenceRecordToExpand = Seq(
    Json.parse(
      """
        {
          "user": {
            "id": "usr-123",
            "discriminator": "user_reference"
          }
        }
      """.stripMargin))

  "expand" should {
    "return expanded user when user exists" in {
      running(FakeApplication()) {
        val user = User("user", identifiedClient)
        val doExpand = user.expand(userReferenceRecordToExpand)

        Await.result(doExpand.map(e =>
          e.head must equal(validExpandRecord.head))
          , Duration(5, "seconds"))
      }
    }

    "return user reference when user does not exist" in {
      running(FakeApplication()) {
        val user = User("user", identifiedClient)
        val doExpand = user.expand(Seq(userReferenceRecord))

        Await.result(doExpand.map(e =>
          e.head must equal(userReferenceRecord))
          , Duration(5, "seconds"))
      }
    }
  }
}
