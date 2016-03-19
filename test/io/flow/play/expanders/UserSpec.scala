package io.flow.play.expanders

import io.flow.common.v0.{models => common}
import io.flow.common.v0.models.json._
import io.flow.play.clients.MockUserClient
import io.flow.play.util.IdGenerator
import io.flow.user.v0.interfaces.{Client => UserClient}
import io.flow.user.v0.models.json._

import org.scalatestplus.play._
import org.mockito.Mockito._

import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{Headers, Result, Request}
import play.api.test.Helpers._


class UserSpec extends PlaySpec with OneAppPerSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val client: MockUserClient = play.api.Play.current.injector.instanceOf[UserClient].asInstanceOf[MockUserClient]

  val mockHeaders = mock(classOf[Headers])
  when(mockHeaders.headers) thenReturn Seq.empty[(String, String)]

  implicit val mockRequest = mock(classOf[Request[Result]])
  when(mockRequest.headers) thenReturn mockHeaders

  def toReference(user: common.User) = common.UserReference(id = user.id)

  def buildUser(): common.User = {
    common.User(
      id = IdGenerator("tst").randomId(),
      email = Some("test@flow.io"),
      name = common.Name(first = Some("John"), last = Some("Smith"))
    )
  }

  
  // TODO: Bind to the specific client instance
  override lazy val app = new GuiceApplicationBuilder().bindings(bind[UserClient].to[MockUserClient]).build()

  "expand" should {
    "return expanded user when user exists" in {
      val user = buildUser()

      client.data.add(user)

      val expanded = await(
        User("user", client).expand(Seq(Json.obj("user" -> Json.toJson(toReference(user)))))
      ).headOption.getOrElse {
        sys.error("Expanded user not found")
      }

      expanded must equal(Json.obj("user" -> Json.toJson(user)))
    }

    "return user reference when user does not exist" in {
      val user = buildUser()

      val expanded = await(
        User("user", client).expand(Seq(Json.obj("user" -> Json.toJson(toReference(user)))))
      ).headOption.getOrElse {
        sys.error("Expanded user not found")
      }

      expanded must equal(Json.obj("user" -> Json.toJson(toReference(user))))
    }

  }
}
