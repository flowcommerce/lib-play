package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.scalatest.{FunSpec, Matchers}

class AuthDataSpec extends FunSpec with Matchers {

  it("AuthData.user") {
    val id = "user-1"
    val auth = AuthHeaders.user(UserReference(id), requestId = "test1")
    auth.requestId should be("test1")
    auth.user.id should be(id)
  }

  it("AuthData.organization defaults") {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo")
    auth.user.id should be("user-1")
    auth.organization should be("demo")
    auth.environment should be(Environment.Sandbox)
    auth.role should be(Role.Member)
  }

  it("AuthData.organization") {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo", Role.Admin, Environment.Production)
    auth.user.id should be("user-1")
    auth.organization should be("demo")
    auth.environment should be(Environment.Production)
    auth.role should be(Role.Admin)
  }

  it("AuthData.generateRequestId") {
    AuthHeaders.generateRequestId("foo").startsWith("foo") should be(true)

    val all = 1.to(100).map { _ => AuthHeaders.generateRequestId("foo") }
    all.distinct.size should be(100)
  }

}
