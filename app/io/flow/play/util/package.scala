package io.flow.play

package object util {
  @deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
  type Config = io.flow.util.Config
  val EnvironmentConfig: Config = io.flow.util.EnvironmentConfig
  val PropertyConfig: Config = io.flow.util.PropertyConfig
  type ChainedConfig = io.flow.util.ChainedConfig
}
