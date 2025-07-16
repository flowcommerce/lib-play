package io.flow.play.standalone

import com.google.inject.Provider
import com.rollbar.notifier.Rollbar
import play.api.{Configuration, Environment, Mode}
import play.api.inject.{bind, _}

import javax.inject.{Inject, Singleton}

// This is duplication of io.flow.play.clients.ConfigModule in lib-play.  Depending on lib-play
// makes publishing more difficult due to versioning.
final class FlowConfigModule
  extends SimpleModule((env, conf) => {
    import FlowConfigModule.{DefaultConfig, MockConfig}
    import io.flow.util.{Config => FlowConfig}

    assert(conf.entrySet.nonEmpty, "Expected configuration to be non empty before provision")

    env.mode match {
      case Mode.Prod | Mode.Dev =>
        Seq(
          bind[FlowConfig].to[DefaultConfig],
        )
      case Mode.Test =>
        Seq(
          bind[FlowConfig].to[MockConfig],
        )
    }
  })

// Duplication of io.flow.play.clients.ConfigModule in lib-play
object FlowConfigModule {
  import io.flow.util.{ChainedConfig, Config, EnvironmentConfig, PropertyConfig}
  import scala.collection.mutable

  /** A chained configuration that favors environment variables, then system properties, then the play application
    * configuration file.
    */
  @Singleton
  private class DefaultConfig @Inject() (
    appConfig: ApplicationConfig,
  ) extends ChainedConfig(Seq(EnvironmentConfig, PropertyConfig, appConfig))

  @Singleton
  private class ApplicationConfig @Inject() (
    underlying: Configuration,
  ) extends Config {

    override def optionalMap(name: String): Option[Map[String, Seq[String]]] =
      underlying.getOptional[Map[String, Seq[String]]](name)

    override def optionalList(name: String): Option[Seq[String]] = {
      underlying.getOptional[Seq[String]](name).map { list =>
        list.map(_.trim)
      }
    }

    override def get(name: String): Option[String] = {
      underlying.getOptional[String](name).map(_.trim).map {
        case "" => {
          sys.error(s"Value for configuration parameter[$name] cannot be blank")
        }
        case value => value
      }
    }
  }

  // TODO - why not singleton
  private class MockConfig @javax.inject.Inject() (
    defaultConfig: DefaultConfig,
  ) extends Config {

    override def optionalMap(name: String): Option[Map[String, Seq[String]]] = {
      values
        .get(name)
        .map {
          _.asInstanceOf[Map[String, Seq[String]]]
        }
        .orElse(defaultConfig.optionalMap(name))
    }

    override def optionalList(name: String): Option[Seq[String]] = {
      values.get(name) match {
        case Some(v) => Some(v.asInstanceOf[Seq[String]])
        case None => defaultConfig.optionalList(name)
      }
    }

    def set(name: String, value: Seq[String]): Unit = {
      values += (name -> value)
    }

    val values: mutable.Map[String, Any] = {
      val d = scala.collection.mutable.Map[String, Any]()
      d += ("JWT_SALT" -> "test")
      d
    }

    def set(name: String, value: String): Unit = {
      values += (name -> value)
    }

    override def get(name: String): Option[String] = {
      values.get(name) match {
        case Some(v) => Some(v.toString)
        case None => defaultConfig.get(name)
      }
    }
  }
}

final class RollbarLifecycleModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] =
    Seq(
      bind[RollbarLifecycleModule.LifecycleHookRunner].toSelf.eagerly(),
    )
}

object RollbarLifecycleModule {
  import scala.concurrent.Future

  @Singleton
  final class LifecycleHookRunner @Inject() (
    rollbarProvider: Provider[Option[Rollbar]],
    applicationLifecycle: ApplicationLifecycle,
  ) {
    rollbarProvider.get().foreach { rollbar =>
      applicationLifecycle.addStopHook { () =>
        Future.successful {
          rollbar.info("Closing Rollbar")
          rollbar.close(true)
        }
      }
    }
  }
}
