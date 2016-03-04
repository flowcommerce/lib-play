package io.flow.play.util

import play.api.Logger
import play.api.Configuration
import scala.util.{Failure, Success, Try}

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

  def requiredLong(name: String): Long = toLong(name, requiredString(name))

  def optionalLong(name: String): Option[Long] = optionalString(name).map(toLong(name, _))

  private[this] def toLong(name: String, value: String): Long = {
    Try(value.toLong) match {
      case Success(v) => v
      case Failure(ex) => {
        val msg = s"Configuration variable[$name] has invalid value[$value]: must be a long"
        Logger.error(msg)
        sys.error(msg)
      }
    }
  }
  
  def requiredInt(name: String): Int = toInt(name, requiredString(name))

  def optionalInt(name: String): Option[Int] = optionalString(name).map(toInt(name, _))

  private[this] def toInt(name: String, value: String): Int = {
    Try(value.toInt) match {
      case Success(v) => v
      case Failure(ex) => {
        val msg = s"Configuration variable[$name] has invalid value[$value]: must be an int"
        Logger.error(msg)
        sys.error(msg)
      }
    }
  }
  
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
  * system properties, then the play application configuration file.
  */
@javax.inject.Singleton
case class DefaultConfig @javax.inject.Inject() (config: Configuration) extends Config {

  private[this] val chain = ChainedConfig(Seq(EnvironmentConfig, PropertyConfig, ApplicationConfig(config)))

  override def optionalString(name: String) = chain.optionalString(name)

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

object PropertyConfig extends Config {

  override def optionalString(name: String): Option[String] = {
    sys.props.get(name).map(_.trim).map { value =>
      value match {
        case "" => {
          val msg = s"Value for system property[$name] cannot be blank"
          Logger.error(msg)
          sys.error(msg)
        }
        case _ => value
      }
    }
  }
}

@javax.inject.Singleton
case class ApplicationConfig @javax.inject.Inject() (configuration: Configuration) extends Config {

  override def optionalString(name: String): Option[String] = {
    configuration.getString(name).map(_.trim).map { value =>
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
