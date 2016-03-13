package io.flow.play.clients

import io.flow.play.util.{IdGenerator, Random}
import io.flow.common.v0.models.UserReference

trait Factories {

  val idGenerator = IdGenerator("test")
  private[this] val random = Random()

  lazy val testUser = makeUserReference()

  def makeUserReference() = {
    UserReference(id = idGenerator.randomId())
  }

  def makeToken() = {
    "test-token-" + random.alphaNumeric(40)
  }

}
