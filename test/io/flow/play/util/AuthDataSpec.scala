package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.scalatest.{FunSpec, Matchers}

class AuthDataSpec extends LibPlaySpec {

  "AuthData.user" in {
    val id = "user-1"
    val auth = AuthHeaders.user(UserReference(id), requestId = "test1")
    auth.requestId must be("test1")
    auth.user.id must be(id)
  }

  "AuthData.organization defaults" in {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo")
    auth.user.id must be("user-1")
    auth.organization must be("demo")
    auth.environment must be(Environment.Sandbox)
    auth.role must be(Role.Member)
  }

  "AuthData.organization" in {
    val auth = AuthHeaders.organization(UserReference("user-1"), "demo", Role.Admin, Environment.Production)
    auth.user.id must be("user-1")
    auth.organization must be("demo")
    auth.environment must be(Environment.Production)
    auth.role must be(Role.Admin)
  }

  "AuthData.generateRequestId" in {
    AuthHeaders.generateRequestId("foo").startsWith("foo") must be(true)

    val all = 1.to(100).map { _ => AuthHeaders.generateRequestId("foo") }
    all.distinct.size must be(100)
  }

}
