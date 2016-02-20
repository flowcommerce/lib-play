package io.flow.play.clients

import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod => Seq(
        bind[Registry].to[ProductionRegistry]
      )
      case Mode.Dev => Seq(
        bind[Registry].to[DevelopmentRegistry]
      )
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

class AuthorizationClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[AuthorizationClient].to[DefaultAuthorizationClient]
      )
      case Mode.Test => Seq(
        bind[AuthorizationClient].to[MockAuthorizationClient]
      )
    }
  }

}
