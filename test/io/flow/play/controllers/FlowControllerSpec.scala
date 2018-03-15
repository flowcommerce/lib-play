package io.flow.play.controllers

import javax.inject.Inject

import io.flow.play.clients.ConfigModule
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.ControllerComponents

class FlowControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ConfigModule)
      .build()

  "FlowController" should {

    "be instantiated" in {
      val controller = app.injector.instanceOf[FlowControllerImpl]
      controller must not be null
      controller.flowControllerComponents must not be null
      controller.controllerComponents must not be null
    }

  }

}

// Mimic usual controller
class FlowControllerImpl @Inject() (
  val flowControllerComponents: FlowControllerComponents,
  val controllerComponents: ControllerComponents
) extends FlowController
