package io.flow.play.controllers

import javax.inject.Inject
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.mvc.ControllerComponents

class FlowControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

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
