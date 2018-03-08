package io.flow.play.controllers

import io.flow.play.clients.ConfigModule
import io.flow.test.utils.FlowPlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class InjectedFlowControllerSpec extends FlowPlaySpec {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ConfigModule)
      .build()

  "InjectedFlowController" should {

    "be instantiated" in {
      val controller = init[InjectedFlowControllerImpl]
      controller must not be null
      controller.Anonymous must not be null
      controller.Action must not be null
    }
  }
}

// Mimic usual controller
class InjectedFlowControllerImpl extends InjectedFlowController

