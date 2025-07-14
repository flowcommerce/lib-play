package io.flow.play.standalone

import com.typesafe.config.Config
import org.slf4j.ILoggerFactory
import play.api.inject._
import play.api.libs.logback.LogbackLoggerConfigurator
import play.api.{Environment, LoggerConfigurator, Configuration => PlayConfiguration}

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
        bind[Config].toInstance(conf.underlying),
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
}
