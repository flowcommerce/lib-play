package io.flow.play.controllers

import io.flow.play.clients.ConfigModule
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class InjectedFlowControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ConfigModule)
      .build()

  "InjectedFlowController" should {

    "be instantiated" in {
      val controller = app.injector.instanceOf[InjectedFlowControllerImpl]
      controller must not be null
      controller.Anonymous must not be null
      controller.Action must not be null
    }
  }
}

// Mimic usual controller
class InjectedFlowControllerImpl extends InjectedFlowController
