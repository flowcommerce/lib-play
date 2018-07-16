package io.flow.play.util

import play.api.Logger
import play.api.Configuration
import scala.util.{Failure, Success, Try}

/**
  * Wrapper on play config testing for empty strings and standardizing
  * error message for required configuration.
  */
@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
trait Config extends io.flow.util.Config

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
case class ChainedConfig(configs: Seq[Config]) extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    configs.find { c =>
      c.optionalList(name).isDefined
    }.flatMap(_.optionalList(name))
  }

  override def get(name: String): Option[String] = {
    configs.find { c =>
      c.optionalString(name).isDefined
    }.flatMap(_.optionalString(name))
  }

}

/**
  * A chained configuration that favors environment variables, then
  * system properties, then the play application configuration file.
  */
@javax.inject.Singleton
case class DefaultConfig @javax.inject.Inject() (appConfig: ApplicationConfig) extends Config {

  private[this] val chain = ChainedConfig(
    Seq(EnvironmentConfig, PropertyConfig, appConfig)
  )

  override def optionalList(name: String): Option[Seq[String]] = chain.optionalList(name)

  override def get(name: String): Option[String] = chain.optionalString(name)

}

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
object EnvironmentConfig extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
    sys.env.get(name).map(_.trim).map {
      case "" => {
        val msg = s"FlowError Value for environment variable[$name] cannot be blank"
        Logger.error(msg)
        sys.error(msg)
      }
      case value => value
    }
  }
}

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
object PropertyConfig extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
    sys.props.get(name).map(_.trim).map {
      case "" => {
        val msg = s"FlowError Value for system property[$name] cannot be blank"
        Logger.error(msg)
        sys.error(msg)
      }
      case value => value
    }
  }
}

@javax.inject.Singleton
case class ApplicationConfig @javax.inject.Inject() (configuration: Configuration) extends Config {

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
