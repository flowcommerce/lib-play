package io.flow.play
import io.flow.util.{clients => libutil}

package object clients {

  type Registry = libutil.Registry
  type ProductionRegistry = libutil.ProductionRegistry
  type MockRegistry = libutil.MockRegistry
}
