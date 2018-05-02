package io.flow.play.util

object Salts {
  private[this] val VarName = "JWT_SALTS"

  def all(config: Config): List[String] = {
    val salts = if (config.optionalString(VarName).isDefined) {
      config.requiredString(VarName).trim.split("\\s+").map(_.trim).filter(_.nonEmpty).toList
    } else {
      List(config.requiredString("JWT_SALT"))
    }

    assert(
      salts.nonEmpty,
      s"Must have at least one value in the $VarName environment variable"
    )
    salts
  }

}
