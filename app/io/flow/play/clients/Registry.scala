package io.flow.play.clients

import io.flow.play.util.Config
import io.flow.registry.v0.{Authorization, Client}
import io.flow.registry.v0.errors.UnitResponse
import io.flow.registry.v0.models.Application
import play.api.{Environment, Logger, Mode}
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

trait RegistryConstants {

  val ProductionDomain = "api.flow.io"
  val TokenVariableName = "io.flow.user.token"

  def log(env: String, applicationId: String, message: String) {
    Logger.info(s"[${getClass.getName} $env] app[$applicationId] $message")
  }

}

/**
  * Production works by convention with no external dependencies.
  */
class ProductionRegistry() extends Registry with RegistryConstants {

  override def host(applicationId: String): String = {
    val host = s"http://${applicationId}.${ProductionDomain}"
    log("Production", applicationId, s"Host[$host]")
    host
  }

}

/**
  * This trait fetches a registry Application - and from there
  * provides the full host using the external port for that
  * application.
  */
trait RegistryApplicationProvider extends Registry with RegistryConstants {

  val DevHostName = "io.flow.dev.host"
  val RegistryHostName = "io.flow.registry.host"

  def config: Config

  /**
    * Name of an environment variable containing the name of the VM
    * Host. This is used for local development and should resolve to
    * the VM of the virtual machine running the docker containers.
    */
  lazy val DevHost = config.optionalString(DevHostName).getOrElse("vm")

  override def host(applicationId: String): String = {
    overridden(applicationId) match {
      case Some(host) => {
        log("Development", applicationId, s"Host[$host] (overridden via environment variable)")
        host
      }

      case None => {
        val port = getById(applicationId).ports.headOption.getOrElse {
          sys.error(s"application[$applicationId] does not have any ports in registry")
        }
        val host = s"http://${DevHost}:${port.external}"
        log("Development", applicationId, s"Host[$host]")
        host
      }
    }
  }

  def getById(applicationId: String): Application

  /**
    * Allows user to set an environment variable to specify the
    * specific host for an application. If found, we use this value as
    * the host for that service. Ex: USER_HOST=http://localhost:6021
    * 
    * Ex:
    *   USER_HOST="http://localhost:6021" sbt
    */
  private[this] def overridden(applicationId: String): Option[String] = {
    config.optionalString(s"${applicationId.toUpperCase}_HOST")
  }
  
}

@javax.inject.Singleton
class DevelopmentRegistry @javax.inject.Inject() (val config: Config) extends RegistryApplicationProvider {
  import scala.concurrent.ExecutionContext.Implicits.global

  private[this] lazy val RegistryHost = {
    val host = config.optionalString(RegistryHostName).getOrElse(s"http://registry.$ProductionDomain")
    log("Development", "registry", s"Host[$host]")
    host
  }

  private[this] lazy val token = config.requiredString(TokenVariableName)

  private[this] lazy val client = new Client(RegistryHost, Some(Authorization.Basic(token)))

  override def getById(applicationId: String): Application = {
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

}

@javax.inject.Singleton
class MockRegistry() extends Registry {

  override def host(applicationId: String) = "http://localhost"

}
