package io.flow.play.util

/**
  * We introduced our own environment primarily to support our
  * dockerized environments and to integrate nicely with the flow
  * registry. The environment is used by the registry to identify
  * hostnames to use in either production or development.
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
  *   1. an environment variable named 'FLOW_ENV'
  *   2. a system property named 'FLOW_ENV'
  *   3. a default of 'development'
  * 
  * Valid values for the environment are: 'development',
  * 'production'
  * 
  * To get the current environment:
  * 
  *     import io.flow.play.util.FlowEnvironmentProvider
  *     class YourClass @javax.inject.Inject() (
  *       flowEnvironmentProvider: FlowEnvironmentProvider
  *     ) {
  *       flowEnvironmentProvider.current match {
  *         case FlowEnvironment.Development => ...
  *         case FlowEnvironment.Production => ...
  *       }
  *     }
  */
class FlowEnvironmentProvider() {

  val current: FlowEnvironment = {
    EnvironmentConfig.optionalString("FLOW_ENV") match {
      case Some(value) => {
        parse("environment variable", value)
      }
      case None => {
        PropertyConfig.optionalString("FLOW_ENV") match {
          case Some(value) => {
            parse("system property", value)
          }
          case None => {
            play.api.Logger.info("Using default flow environment[development]. To override, specify environment variable or system property named[FLOW_ENV]")
            FlowEnvironment.Development
          }
        }
      }
    }
  }

  private[util] def parse(source: String, value: String): FlowEnvironment = {
    FlowEnvironment.fromString(value) match {
      case Some(env) => {
        play.api.Logger.info(s"Set flow environment to[$env] from $source[FLOW_ENV]")
        env
      }
      case None => {
        val message = s"Value[$value] from $source[FLOW_ENV] is invalid. Valid values are: " + FlowEnvironment.all.map(_.toString).mkString(", ")
        play.api.Logger.error(message)
        sys.error(message)
      }
    }
  }
  
}
