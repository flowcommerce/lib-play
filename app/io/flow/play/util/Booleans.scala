package io.flow.play.util

/**
  * Defines the standard strings we look for in terms of parsing
  * booleans. Aligns to the values used also by apidoc - helpful for
  * things like csv import, parsing attribute values, etc.
  */
object Booleans {

  val TrueValues = Seq("t", "true", "y", "yes", "on", "1", "trueclass")
  val FalseValues = Seq("f", "false", "n", "no", "off", "0", "falseclass")

  /**
    * Parses the provided string as a boolean returning either a
    * boolean value or none if the value is not recognized.
    */
  def parse(value: String): Option[Boolean] = {
    val formatted = value.trim.toLowerCase
    if (Booleans.TrueValues.contains(formatted)) {
      Some(true)
    } else if (Booleans.FalseValues.contains(formatted)) {
      Some(false)
    } else {
      None
    }
  }

}
