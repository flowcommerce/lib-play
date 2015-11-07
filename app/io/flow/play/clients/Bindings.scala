package io.flow.play.clients

import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

class UserTokenClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[UserTokenClient].to[DefaultUserTokenClient]
      )
      case Mode.Test => Seq(
        bind[UserTokenClient].to[MockUserClient]
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
