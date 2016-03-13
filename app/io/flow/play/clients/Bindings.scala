package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig, FlowEnvironment}
import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[Registry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development => Seq(
            bind[Registry].to[DevelopmentRegistry]
          )
        }
      }
      case Mode.Test => Seq(
        bind[Registry].to[MockRegistry]
      )
    }
  }

}

class ConfigModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[Config].to[DefaultConfig]
    )
  }

}

class TokenClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[TokenClient].to[DefaultTokenClient]
      )
      case Mode.Test => Seq(
        bind[TokenClient].to[MockTokenClient]
      )
    }
  }

}
