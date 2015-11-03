package io.flow.play.util

import play.api.Logger
import play.api.Play.current

/**
  * Wrapper on play config testing for empty strings and standardizing
  * error message for required configuration.
  */
object Config {

  def requiredString(name: String): String = {
    optionalString(name).getOrElse {
      val msg = s"configuration parameter[$name] is required"
      Logger.error(msg)
      sys.error(msg)
    }
  }

  def optionalString(name: String): Option[String] = {
    current.configuration.getString(name).map { value =>
      if (value.trim == "") {
        val msg = s"Value for configuration parameter[$name] cannot be blank"
        Logger.error(msg)
        sys.error(msg)
      }
      value
    }
  }
}
