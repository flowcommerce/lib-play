package io.flow.play

package object clients {
  type Registry = io.flow.util.clients.Registry
  type ProductionRegistry = io.flow.util.clients.ProductionRegistry
  type K8sProductionRegistry = io.flow.util.clients.K8sProductionRegistry
  type MockRegistry = io.flow.util.clients.MockRegistry

  val RegistryConstants = io.flow.util.clients.RegistryConstants
}
