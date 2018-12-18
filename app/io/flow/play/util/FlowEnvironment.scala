package io.flow.play.util

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
sealed trait FlowEnvironment

/**
  * We introduced our own environment primarily to support our
  * dockerized environments and to integrate nicely with the flow
  * registry. The environment is used by the registry to identify
  * hostnames to use in either production, development or workstation,
  * and within workstation, we needed a way to reliably identify our
  * intended environment as opposed to the Play environment.
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
  * 'workstation', 'production'
  * 
  * To get the current environment:
  * 
  *     import io.flow.play.util.FlowEnvironment
  *
  *     FlowEnvironment.Current match {
  *       case FlowEnvironment.Development => ...
  *       case FlowEnvironment.Workstation => ...
  *       case FlowEnvironment.Production => ...
  *     }
  */
@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
object FlowEnvironment {

  case object Development extends FlowEnvironment { override def toString = "development" }
  case object Production extends FlowEnvironment { override def toString = "production" }
  case object Workstation extends FlowEnvironment { override def toString = "workstation" }

  val all: Seq[FlowEnvironment] = Seq(Development, Production, Workstation)

  private[this]
  val byName = all.map(x => x.toString.toLowerCase -> x).toMap

  def fromString(value: String): Option[FlowEnvironment] = byName.get(value.toLowerCase)

  val Current: FlowEnvironment = {
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
            FlowEnvironment.Development
          }
        }
      }
    }
  }

  private[util] def parse(source: String, value: String): FlowEnvironment = {
    FlowEnvironment.fromString(value) match {
      case Some(env) => {
        env
      }
      case None => {
        sys.error(s"Value[$value] from $source[FLOW_ENV] is invalid. Valid values are: " + all.map(_.toString).mkString(", "))
      }
    }
  }
  
}
