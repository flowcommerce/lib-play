package io.flow.play

package object util {
  type Config = io.flow.util.Config
  type ChainedConfig = io.flow.util.ChainedConfig

  val EnvironmentConfig: Config = io.flow.util.EnvironmentConfig
  val PropertyConfig: Config = io.flow.util.PropertyConfig
}
