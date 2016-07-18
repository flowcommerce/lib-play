package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig, FlowEnvironment}
import io.flow.token.v0.interfaces.{Client => TokenClient}
import io.flow.user.v0.interfaces.{Client => UserClient}
import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

class ConfigModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(bind[Config].to[DefaultConfig])
      case Mode.Test => Seq(bind[Config].to[MockConfig])
    }
  }

}

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[Registry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development | FlowEnvironment.Workstation => Seq(
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

class UserClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[UserClient].to[DefaultUserClient]
      )
      case Mode.Test => Seq(
        bind[UserClient].to[MockUserClient]
      )
    }
  }

}
