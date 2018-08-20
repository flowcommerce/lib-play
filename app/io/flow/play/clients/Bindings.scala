package io.flow.play.clients

import io.flow.util.Config
import io.flow.play.util.{Config => FlowPlayConfig, DefaultConfig, FlowEnvironment}
import io.flow.token.v0.interfaces.{Client => TokenClient}
import io.flow.user.v0.interfaces.{Client => UserClient}
import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module
import io.flow.play.clients.{Registry => FlowRegistry}

class ConfigModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[Config].to[DefaultConfig],
        bind[FlowPlayConfig].to[DefaultConfig]
      )
      case Mode.Test => Seq(
        bind[Config].to[MockConfig],
        bind[FlowPlayConfig].to[MockConfig]
      )
    }
  }

}

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[io.flow.util.clients.Registry].to[ProductionRegistry],
            bind[FlowRegistry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development | FlowEnvironment.Workstation => Seq(
            bind[io.flow.util.clients.Registry].to[DevelopmentRegistry],
            bind[FlowRegistry].to[DevelopmentRegistry]
          )
        }
      }
      case Mode.Test => Seq(
        bind[io.flow.util.clients.Registry].to[MockRegistry],
        bind[FlowRegistry].to[MockRegistry]
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
