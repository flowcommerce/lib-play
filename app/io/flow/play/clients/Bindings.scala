package io.flow.play.clients

import io.flow.play.util.FlowEnvironment
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

class UserTokensClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[UserTokensClient].to[DefaultUserTokensClient]
      )
      case Mode.Test => Seq(
        bind[UserTokensClient].to[MockUserTokensClient]
      )
    }
  }

}
