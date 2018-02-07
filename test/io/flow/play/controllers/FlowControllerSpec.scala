package io.flow.play.controllers

import javax.inject.Inject

import io.flow.play.clients.ConfigModule
import io.flow.play.util.Config
import io.flow.test.utils.FlowPlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.ControllerComponents

class FlowControllerSpec extends FlowPlaySpec {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ConfigModule)
      .build()

  "FlowController" should {

    "be instantiated" in {
      val controller = init[FlowControllerImpl]
      controller must not be null
      controller.flowControllerComponents must not be null
    }

  }

}

class FlowControllerImpl @Inject() (
  val config: Config,
  val flowControllerComponents: FlowControllerComponents,
  val controllerComponents: ControllerComponents
) extends FlowController {}
