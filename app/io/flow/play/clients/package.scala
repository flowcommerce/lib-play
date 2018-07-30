package io.flow.play

import io.flow.util.clients.{Registry => NewRegistry, ProductionRegistry => NewProductionRegistry, MockRegistry => NewMockRegistry}

package object clients {
  @deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
  type Registry = NewRegistry

  @deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
  type ProductionRegistry = NewProductionRegistry

  @deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
  type MockRegistry = NewMockRegistry
}
