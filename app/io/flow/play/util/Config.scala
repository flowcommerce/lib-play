package io.flow.play.util

import play.api.Logger
import play.api.Play.current

/**
  * Wrapper on play config testing for empty strings and standardizing
  * error message for required configuration.
  */
trait Config {

  def requiredString(name: String): String = {
    optionalString(name).getOrElse {
      val msg = s"Configuration variable[$name] is required"
      Logger.error(msg)
      sys.error(msg)
    }
  }

  def optionalString(name: String): Option[String]

}

case class ChainedConfig(configs: Seq[Config]) extends Config {

  override def optionalString(name: String): Option[String] = {
    configs.find { c =>
      !c.optionalString(name).isEmpty
    }.flatMap(_.optionalString(name))
  }

}

/**
  * A chained configuration that favors environment variables, then
  * the play application configuration file.
  */
object DefaultConfig extends Config {

  private[this] val config = ChainedConfig(Seq(EnvironmentConfig, ApplicationConfig))

  override def optionalString(name: String) = config.optionalString(name)

}

object EnvironmentConfig extends Config {

  override def optionalString(name: String): Option[String] = {
    sys.env.get(name).map(_.trim).map { value =>
      value match {
        case "" => {
          val msg = s"Value for environment variable[$name] cannot be blank"
          Logger.error(msg)
          sys.error(msg)
        }
        case _ => value
      }
    }
  }
}

object ApplicationConfig extends Config {

  override def optionalString(name: String): Option[String] = {
    current.configuration.getString(name).map(_.trim).map { value =>
      value match {
        case "" => {
          val msg = s"Value for configuration parameter[$name] cannot be blank"
          Logger.error(msg)
          sys.error(msg)
        }
        case _ => value
      }
    }
  }
}
