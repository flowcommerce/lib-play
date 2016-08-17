package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.scalatest.{FunSpec, Matchers}

class AuthDataSpec extends FunSpec with Matchers {

  it("AuthData.user") {
    val id = "user-1"
    val auth = AuthData.user(UserReference(id))
    auth.user.id should be(id)
    auth.organization should be(None)
  }

  it("AuthData.organization defaults") {
    val auth = AuthData.organization(UserReference("user-1"), "demo")
    auth.user.id should be("user-1")
    auth.organization should be(
      Some(
        OrganizationAuthData("demo", Role.Member, Environment.Sandbox)
      )
    )
  }

  it("AuthData.organization") {
    val auth = AuthData.organization(UserReference("user-1"), "demo", Role.Admin, Environment.Production)
    auth.user.id should be("user-1")
    auth.organization should be(
      Some(
        OrganizationAuthData("demo", Role.Admin, Environment.Production)
      )
    )
  }

}
