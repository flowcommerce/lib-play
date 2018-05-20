package io.flow.play.controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

class InjectedFlowControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

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

