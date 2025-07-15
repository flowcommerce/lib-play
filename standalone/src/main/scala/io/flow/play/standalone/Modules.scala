package io.flow.play.standalone

import com.typesafe.config.{Config => TypesafeConfig}
import org.slf4j.ILoggerFactory
import play.api.inject._
import play.api.libs.logback.LogbackLoggerConfigurator
import play.api.{Configuration, Environment, LoggerConfigurator, Mode, Configuration => PlayConfiguration}

import scala.collection.mutable

object Modules {
  // Subset of bindings in play.api.inject.BuiltinModule
  final class PlayApplicationLifecycleModule
    extends SimpleModule((_, _) => {
      Seq(
        bind[DefaultApplicationLifecycle].toSelf,
        bind[ApplicationLifecycle].to(bind[DefaultApplicationLifecycle]),
      )
    })

  // Subset of bindings in play.api.inject.BuiltinModule
  final class PlayConfigurationModule
    extends SimpleModule((env, conf) => {

      assert(conf.entrySet.nonEmpty, "Expected configuration to be non empty before provision")

      Seq(
        bind[Environment] to env,
        bind[ConfigurationProvider].toInstance(new ConfigurationProvider(conf)),
        bind[PlayConfiguration].toInstance(conf),
        bind[TypesafeConfig].toInstance(conf.underlying),
      )
    })

  final class SLFLoggerConfigurationModule
    extends SimpleModule((env, conf) => {
      assert(conf.entrySet.nonEmpty, "Expected configuration to be non empty before provision")

      // GuiceApplicationBuilder.configureLoggerFactory
      LoggerConfigurator(env.classLoader) match {
        case Some(configurator) =>
          assert(configurator.isInstanceOf[LogbackLoggerConfigurator], "Expected LogbackLoggerConfigurator")
          configurator.configure(env, conf, Map.empty)
          configurator.loggerFactory
          Seq(
            bind[ILoggerFactory].toInstance(configurator.loggerFactory),
          )
        case None => sys.error("No logback configuration found")
      }
    })

  // Duplication of io.flow.play.clients.ConfigModule in lib-play.  Depending on lib-play makes
  // publishing more difficult due to versioning.
  final class FlowConfigurationModule
    extends SimpleModule((env, conf) => {
      import FlowConfigurationModule.{DefaultConfig, MockConfig}
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
  object FlowConfigurationModule {
    import io.flow.util.{ChainedConfig, Config, EnvironmentConfig, PropertyConfig}

    import javax.inject.{Inject, Singleton}

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
}
