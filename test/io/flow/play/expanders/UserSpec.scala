package io.flow.play.expanders

import io.flow.play.clients.MockUserClient
import org.scalatest.{Matchers, FunSpec}
import play.api.libs.json.Json


class UserSpec extends FunSpec with Matchers {
  val user = User("user", MockUserClient)

  describe("expand") {

    val records = Seq(Json.parse((
      """
        |{
          |"id": "usr-20160115-988044202",
          |"email": "\"test@flow.io\"",
          |"name": {
          | "first": testFirst,
          | "last": testLast
          |},
          |"discriminator": "user"
        |}
      """.stripMargin
      )))
    it("expand UserReference") {
      val key = user.expand()()
      urlKey.validate(key) should be(Nil)
    }
}
