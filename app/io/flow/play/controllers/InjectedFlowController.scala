package io.flow.play.controllers

import javax.inject.Inject

import play.api.mvc.InjectedController

/** An alternative of [[FlowController]] that relies on dependency injection to get the component instances.
  * Implementation adapted from [[play.api.mvc.InjectedController]].
  */
trait InjectedFlowController extends FlowController with InjectedController {
  private[this] var _flowControllerComponents: FlowControllerComponents = _

  protected def flowControllerComponents: FlowControllerComponents =
    if (_flowControllerComponents == null) fallbackFlowControllerComponents else _flowControllerComponents

  /** Call this method to set the [[FlowControllerComponents]] instance.
    */
  @Inject
  def setFlowControllerComponents(components: FlowControllerComponents): Unit = {
    _flowControllerComponents = components
  }

  /** Defines fallback components to use in case setFlowControllerComponents has not been called.
    */
  protected def fallbackFlowControllerComponents: FlowControllerComponents = {
    throw new NoSuchElementException(
      "FlowControllerComponents not set! Call setFlowControllerComponents or create the instance with dependency injection."
    )
  }
}
