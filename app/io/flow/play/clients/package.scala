package io.flow.play

import io.flow.util.clients.{Registry => NewRegistry, ProductionRegistry => NewProductionRegistry, MockRegistry => NewMockRegistry}

package object clients {
  type Registry = NewRegistry
  type ProductionRegistry = NewProductionRegistry
  type MockRegistry = NewMockRegistry
}
