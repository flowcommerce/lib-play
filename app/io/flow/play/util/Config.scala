package io.flow.play.util

import play.api.Logger
import play.api.Configuration

/**
  * A chained configuration that favors environment variables, then
  * system properties, then the play application configuration file.
  */
@javax.inject.Singleton
class DefaultConfig @javax.inject.Inject() (appConfig: ApplicationConfig)
  extends ChainedConfig(Seq(EnvironmentConfig, PropertyConfig, appConfig))

@javax.inject.Singleton
class ApplicationConfig @javax.inject.Inject() (configuration: Configuration) extends Config {

   override def optionalList(name: String): Option[Seq[String]] = {
    configuration.getOptional[Seq[String]](name).map { list =>
      list.map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
    configuration.getOptional[String](name).map(_.trim).map {
      case "" => {
        val msg = s"FlowError Value for configuration parameter[$name] cannot be blank"
        Logger.error(msg)
        sys.error(msg)
      }
      case value => value
    }
  }
}
