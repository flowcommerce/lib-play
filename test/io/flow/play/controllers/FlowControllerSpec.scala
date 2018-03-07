package io.flow.play.controllers

import io.flow.play.clients.ConfigModule
import io.flow.test.utils.FlowPlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class FlowControllerSpec extends FlowPlaySpec {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ConfigModule)
      .build()

  "FlowController" should {

    "be instantiated" in {
      val controller = init[FlowControllerImpl]
      controller must not be null
      controller.Identified must not be null
    }

  }

}

// Mimic usual controller
class FlowControllerImpl extends FlowController
