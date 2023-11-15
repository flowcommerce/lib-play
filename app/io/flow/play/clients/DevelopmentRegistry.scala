package io.flow.play.clients

import io.flow.play.util.Config
import io.flow.registry.v0.Client
import io.flow.registry.v0.errors.UnitResponse
import io.flow.registry.v0.models.Application
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@javax.inject.Singleton
class DevelopmentRegistry @javax.inject.Inject() (
  config: Config,
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
        val host = RegistryConstants.developmentHost(port.external)
        RegistryConstants.log("Development", applicationId, s"Host[$host] (can override via env var[$varName])")
        host
      }
    }
  }

  protected def getById(applicationId: String): Application = {
    import scala.concurrent.ExecutionContext.Implicits.global

    Await.result(
      client.applications
        .getById(applicationId)
        .map { app =>
          app
        }
        .recover {
          case UnitResponse(401) =>
            sys.error(s"Unauthorized to fetch application[$applicationId] from registry at $RegistryHost")
          case UnitResponse(404) => sys.error(s"application[$applicationId] not found in registry at $RegistryHost")
          case ex: Throwable => throw new Exception(s"ERROR connecting to registry at $RegistryHost", ex)
        },
      Duration(5, "seconds"),
    )
  }

  /** Allows user to set an environment variable to specify the specific host for an application. If found, we use this
    * value as the host for that service. Ex: USER_HOST=http://localhost:6021
    *
    * Ex: USER_HOST="http://localhost:6021" sbt
    */
  protected def overridden(applicationId: String): Option[String] = {
    config.optionalString(overriddeVariableName(applicationId))
  }

  protected def overriddeVariableName(applicationId: String): String = {
    s"${applicationId.toUpperCase}_HOST"
  }
}
