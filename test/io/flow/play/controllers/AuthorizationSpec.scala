package io.flow.play.controllers

import io.flow.play.clients.JwtModule
import io.flow.play.jwt.JwtService
import io.flow.play.util.LibPlaySpec
import org.scalatest.OptionValues
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class AuthorizationSpec extends LibPlaySpec with OptionValues {

  override def fakeApplication(): Application = {
    val builder = new GuiceApplicationBuilder()
    builder.overrides(new JwtModule(builder.environment, builder.configuration)).build()
  }

  private val jwtService = app.injector.instanceOf[JwtService]
  private val authorization = app.injector.instanceOf[AuthorizationImpl]

  "Authorization" should {

    "decode a basic auth header" in {
      val headerValue = "Basic YWRtaW46"
      authorization.get(headerValue).value mustBe Token("admin")
    }

    "decode a bearer jwt" in {
      val encoded = jwtService.encode(Map("id" -> "usr-20160130-1"))
      val headerValue = s"Bearer $encoded"

      authorization.get(headerValue).value mustBe JwtToken("usr-20160130-1")
    }

    "fail to decode a malformed jwt" in {
      val headerValue = "Bearer malformed"
      authorization.get(headerValue) mustBe None
    }

  }

}
