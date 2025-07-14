package io.flow.play.expanders

import io.flow.common.v0.models.json._
import io.flow.common.v0.{models => common}
import io.flow.play.clients.MockUserClient
import io.flow.play.util.LibPlaySpec
import io.flow.util.IdGenerator
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class UserSpec extends LibPlaySpec with FutureAwaits with DefaultAwaitTimeout {

  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val client: MockUserClient = new MockUserClient

  def toReference(user: common.User) = common.UserReference(id = user.id)

  def buildUser(): common.User = {
    common.User(
      id = IdGenerator("tst").randomId(),
      email = Some("test@flow.io"),
      name = common.Name(first = Some("John"), last = Some("Smith")),
    )
  }

  "expand" should {
    "return expanded user when user exists" in {
      val user = buildUser()
      client.data.add(user)

      val expanded = await(
        User("user", client).expand(Seq(Json.obj("user" -> Json.toJson(toReference(user))))),
      ).headOption.getOrElse {
        sys.error("Expanded user not found")
      }

      expanded must equal(Json.obj("user" -> Json.toJson(user)))
    }

    "return user reference when user does not exist" in {
      val user = buildUser()

      val expanded = await(
        User("user", client).expand(Seq(Json.obj("user" -> Json.toJson(toReference(user))))),
      ).headOption.getOrElse {
        sys.error("Expanded user not found")
      }

      expanded must equal(Json.obj("user" -> Json.toJson(toReference(user))))
    }

  }
}
