package io.flow.play.clients

import io.flow.play.util.DefaultConfig
import io.flow.registry.v0.{Authorization, Client}
import io.flow.registry.v0.errors.UnitResponse
import play.api.{Environment, Mode}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * This class implements service discovery for flow based on the
  * environment in which we are in. In production, hostnames are build
  * using convention (e.g. 'user' => 'user.api.flow.io'). In
  * development, hostnames are built by querying the registry for port
  * mappings.
  * 
  * Examples:
  *
  *    lazy val anonymousClient: Client = new Registry(env).withHost("user") { new Client(_) }
  *
  *    lazy val authenticatedClient: Client = new Registry(env).withHostAndToken("user") { (host, token) =>
  *      new Client(host, Some(Authorization.Basic(token)))
  *    }
  * 
  */
@javax.inject.Singleton
class Registry(env: Environment) {

  /**
    * Name of an environment variable containing the name of the VM
    * Host. This is used for local development and should resolve to
    * the VM of the virtual machine running the docker containers.
    */
  private[this] val DEV_HOST = DefaultConfig.optionalString("io.flow.dev.host").getOrElse("vm")

  // TODO: Enable once ready
  // private[this] val RegistryHost = "registry.api.flow.io"
  private[this] val RegistryHost = DefaultConfig.optionalString("io.flow.registry.host").getOrElse(s"http://${DEV_HOST}:6011")


  /**
    * This is the token to identify the user making the API calls.
    */
  private[this] lazy val token = DefaultConfig.requiredString("io.flow.user.token")

  private[this] lazy val client = new Client(RegistryHost, Some(Authorization.Basic(token)))

  /**
    * Executes your function with (host, token) as parameters.
    */
  def withHostAndToken[T](
    applicationId: String
  ) (
    f: (String, String) => T
  ): T = {
    env.mode match {
      case Mode.Prod => {
        f(s"http://${applicationId}.api.flow.io", token)
      }

      case Mode.Dev => {
        import scala.concurrent.ExecutionContext.Implicits.global

        Await.result(
          client.applications.getById(applicationId).map { app =>
            val port = app.ports.headOption.getOrElse {
              sys.error(s"application[$applicationId] does not have any ports in registry at $RegistryHost")
            }
            println("withHostAndToken($applicationId): http://vm:${port.external}")
            f("http://vm:${port.external}", token)
          }.recover {
            case UnitResponse(401) => sys.error(s"Unauthorized to fetch application[$applicationId] from registry at $RegistryHost")
            case UnitResponse(404) => sys.error(s"application[$applicationId] not found in registry at $RegistryHost")
            case ex: Throwable => throw ex
          }
        , Duration(5, "seconds"))
      }
    }
  }

  /**
    * Executes your function with (host) as parameter.
    */
  def withHost[T](
    applicationId: String
  ) (
    f: String => T
  ): T = {
    withHostAndToken(applicationId) { (host, token) =>
      f(host)
    }
  }

}
