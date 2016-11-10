package io.flow.play.util

import play.api.Logger
import play.api.Configuration
import scala.util.{Failure, Success, Try}

/**
  * Wrapper on play config testing for empty strings and standardizing
  * error message for required configuration.
  */
trait Config {

  def optionalList(name: String): Option[Seq[String]]

  def requiredList(name: String): Seq[String] = mustGet(name, optionalList(name))

  /**
    * Return the value for the configuration parameter with the specified name
    */
  def get(name: String): Option[String]

  def requiredString(name: String): String = mustGet(name, optionalString(name))

  def optionalString(name: String): Option[String] = get(name).map(_.trim) match {
    case Some("") => None
    case v => v
  }

  def requiredPositiveLong(name: String): Long = mustGet(name, optionalPositiveLong(name))

  def optionalPositiveLong(name: String): Option[Long] = optionalLong(name) match {
    case None => None
    case Some(v) => v > 0 match {
      case true => Some(v)
      case false => sys.error(s"Configuration variable[$name] has invalid value[$v]: must be > 0")
    }
  }

  def requiredLong(name: String): Long = mustGet(name, optionalLong(name))

  def optionalLong(name: String): Option[Long] = optionalString(name).map { value =>
    Try(value.toLong) match {
      case Success(v) => v
      case Failure(ex) => {
        val msg = s"Configuration variable[$name] has invalid value[$value]: must be a long"
        Logger.error(msg)
        sys.error(msg)
      }
    }
  }
  

  def requiredPositiveInt(name: String): Int = mustGet(name, optionalPositiveInt(name))

  def optionalPositiveInt(name: String): Option[Int] = optionalInt(name) match {
    case None => None
    case Some(v) => v > 0 match {
      case true => Some(v)
      case false => sys.error(s"Configuration variable[$name] has invalid value[$v]: must be > 0")
    }
  }

  def requiredInt(name: String): Int = mustGet(name, optionalInt(name))

  def optionalInt(name: String): Option[Int] = optionalString(name).map { value =>
    Try(value.toInt) match {
      case Success(v) => v
      case Failure(ex) => {
        val msg = s"Configuration variable[$name] has invalid value[$value]: must be an int"
        Logger.error(msg)
        sys.error(msg)
      }
    }
  }
  
  def requiredBoolean(name: String): Boolean = mustGet(name, optionalBoolean(name))

  def optionalBoolean(name: String): Option[Boolean] = optionalString(name).map { value =>
    value.toLowerCase match {
      case "true" | "t" => true
      case "false" | "f" => false
      case other => {
        val msg = s"Configuration variable[$name] has invalid value[$value]: must be true, t, false, or f"
        Logger.error(msg)
        sys.error(msg)
      }
    }
  }
  
  private[this] def mustGet[T](name: String, value: Option[T]): T = {
    value.getOrElse {
      sys.error(s"Configuration variable[$name] is required")
    }
  }

}

case class ChainedConfig(configs: Seq[Config]) extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
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
case class DefaultConfig @javax.inject.Inject() (appConfig: ApplicationConfig) extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  private[this] val chain = ChainedConfig(
    Seq(EnvironmentConfig, PropertyConfig, appConfig)
  )

  override def get(name: String) = chain.optionalString(name)

}

object EnvironmentConfig extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
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

  override def optionalList(name: String): Option[Seq[String]] = {
    get(name).map { text =>
      text.split(",").map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
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

  override def optionalList(name: String): Option[Seq[String]] = {
    configuration.getStringSeq(name).map { list =>
      list.map(_.trim)
    }
  }

  override def get(name: String): Option[String] = {
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
