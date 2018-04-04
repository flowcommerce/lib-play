package io.flow.play.clients

import io.flow.play.util.{Config, EnvironmentConfig, FlowEnvironment, PropertyConfig}
import io.flow.registry.v0.{Authorization, Client}
import io.flow.registry.v0.errors.UnitResponse
import io.flow.registry.v0.models.Application
import play.api.Logger
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * This class implements service discovery for flow based on the
  * environment in which we are in. In production, hostnames are build
  * using convention (e.g. 'user' => 'user.api.flow.io'). In
  * development, hostnames are built by querying the registry for port
  * mappings.
  * 
  * Example:
  *
  *    lazy val client = new Client(new Registry(env).host("user"))
  */
trait Registry {

  /**
    * Returns the host of the application
    * (e.g. http://user.api.flow.io or http://vm:6011)
    */
  def host(applicationId: String): String

}

object RegistryConstants {

  val ProductionDomain = "api.flow.io"

  val WorkstationHostVariableName = "WORKSTATION_HOST"

  val DefaultWorkstationHost = "ws"

  /**
    * Defaults to the workstation host
    */
  private[this] lazy val devHost: String = workstationHost

  /**
    * The resolved name of the host used in workstation
    */
  private[this] lazy val workstationHost: String = {
    EnvironmentConfig.optionalString(WorkstationHostVariableName).getOrElse {
      PropertyConfig.optionalString(WorkstationHostVariableName).getOrElse {
        Logger.info(s"[${getClass.getName}] defaulting workstationHost to '$DefaultWorkstationHost' (can override via env var[$WorkstationHostVariableName])")
        DefaultWorkstationHost
      }
    }
  }

  def log(env: String, applicationId: String, message: String) {
    Logger.info(s"[${getClass.getName} $env] app[$applicationId] $message")
  }

  /**
    * Returns the hostname of the specified application in the
    * production environment.
    */
  def productionHost(applicationId: String): String = {
    s"https://${applicationId}.${ProductionDomain}"
  }

  def developmentHost(applicationId: String, port: Long): String = {
    s"http://$devHost:$port"
  }

  def workstationHost(applicationId: String, port: Long): String = {
    s"http://$workstationHost:$port"
  }

  def host(applicationId: String, port: Long) = {
    FlowEnvironment.Current match {
      case FlowEnvironment.Production => productionHost(applicationId)
      case FlowEnvironment.Development => developmentHost(applicationId, port)
      case FlowEnvironment.Workstation => workstationHost(applicationId, port)
    }
  }

}

/**
  * Production works by convention with no external dependencies.
  */
class ProductionRegistry() extends Registry {

  override def host(applicationId: String) = {
    val host = RegistryConstants.productionHost(applicationId)
    RegistryConstants.log("Production", applicationId, s"Host[$host]")
    host
  }

}

@javax.inject.Singleton
class DevelopmentRegistry @javax.inject.Inject() (
  config: Config
) extends Registry {

  private[this] lazy val RegistryHost: String = {
    val applicationId = "registry"
    val varName = overriddeVariableName(applicationId)

    overridden(applicationId) match {
      case Some(host) => {
        RegistryConstants.log("Development", applicationId, s"Host[$host] (overridden via env var[$varName])")
        host
      }

      case None => {
        val host = RegistryConstants.productionHost(applicationId)
        RegistryConstants.log("Development", applicationId, s"Host[$host] (can override via env var[$varName])")
        host
      }
    }
  }

  private[this] lazy val client = new Client(RegistryHost)

  override def host(applicationId: String): String = {
    val varName = overriddeVariableName(applicationId)

    overridden(applicationId) match {
      case Some(host) => {
        RegistryConstants.log("Development", applicationId, s"Host[$host] (overridden via env var[$varName])")
        host
      }

      case None => {
        val port = getById(applicationId).ports.headOption.getOrElse {
          sys.error(s"application[$applicationId] does not have any ports in registry")
        }
        val host = RegistryConstants.developmentHost(applicationId, port.external)
        RegistryConstants.log("Development", applicationId, s"Host[$host] (can override via env var[$varName])")
        host
      }
    }
  }

  protected def getById(applicationId: String): Application = {
    import scala.concurrent.ExecutionContext.Implicits.global

    Await.result(
      client.applications.getById(applicationId).map { app =>
        app
      }.recover {
        case UnitResponse(401) => sys.error(s"Unauthorized to fetch application[$applicationId] from registry at $RegistryHost")
        case UnitResponse(404) => sys.error(s"application[$applicationId] not found in registry at $RegistryHost")
        case ex: Throwable => throw new Exception(s"ERROR connecting to registry at $RegistryHost", ex)
      }
      , Duration(5, "seconds")
    )
  }

  /**
    * Allows user to set an environment variable to specify the
    * specific host for an application. If found, we use this value as
    * the host for that service. Ex: USER_HOST=http://localhost:6021
    * 
    * Ex:
    *   USER_HOST="http://localhost:6021" sbt
    */
  protected def overridden(applicationId: String): Option[String] = {
    config.optionalString(overriddeVariableName(applicationId))
  }

  protected def overriddeVariableName(applicationId: String): String = {
    s"${applicationId.toUpperCase}_HOST"
  }
}

@javax.inject.Singleton
class MockRegistry() extends Registry {

  override def host(applicationId: String) = s"http://$applicationId.localhost"

}
