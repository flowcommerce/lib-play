package io.flow.play.util

sealed trait FlowEnvironment

/**
  * We introduced our own environment primarily to support our
  * dockerized environments and to integrate nicely with the flow
  * registry. The environment is used by the registry to identify
  * hostnames to use in either production or development, and within
  * development, we needed a way to reliably identify our intended
  * environment as opposed to the Play environment.
  * 
  * Specifically, we use sbt stage to create the run scripts for
  * play. These scripts are the entrypoints in the docker containers
  * we use at flow. These scripts in turn start play with the main
  * class play.core.server.ProdServerStart which specifies:
  * 
  *     val environment = Environment(config.rootDir, process.classLoader, Mode.Prod)
  * 
  * Thus anytime we start play in a docker container, its internal
  * environment will be set to production. The Flow environment is
  * determined by:
  * 
  *   1. an environment variable named 'env'
  *   2. a system property named 'env'
  *   3. a default of 'development'
  * 
  * Valid values for the environment are: 'development' or 'production'
  * 
  * To get the current environment:
  * 
  *     import io.flow.play.util.FlowEnvironment
  *
  *     FlowEnvironment.Current match {
  *       case FlowEnvironment.Development => ...
  *       case FlowEnvironment.Production => ...
  *     }
  */
object FlowEnvironment {

  case object Development extends FlowEnvironment { override def toString() = "development" }

  case object Production extends FlowEnvironment { override def toString() = "production" }

  val all = Seq(Development, Production)

  private[this]
  val byName = all.map(x => x.toString.toLowerCase -> x).toMap

  def fromString(value: String): Option[FlowEnvironment] = byName.get(value.toLowerCase)

  val Current = {
    EnvironmentConfig.optionalString("env") match {
      case Some(value) => {
        parse("environment variable", value)
      }
      case None => {
        PropertyConfig.optionalString("env") match {
          case Some(value) => {
            parse("system property", value)
          }
          case None => {
            play.api.Logger.info("Using default flow environment[development]. To override, specify environment variable or system property named[env]")
            FlowEnvironment.Development
          }
        }
      }
    }
  }

  private[this] def parse(value: String, source: String): FlowEnvironment = {
    FlowEnvironment.fromString(value) match {
      case Some(env) => {
        play.api.Logger.info(s"Set flow environment to[$env] from $source[env]")
        env
      }
      case None => {
        val message = s"Value[$value] from $source[env] is invalid. Valid values are: " + all.map(_.toString).mkString(", ")
        play.api.Logger.error(message)
        sys.error(message)
      }
    }
  }
  
}
