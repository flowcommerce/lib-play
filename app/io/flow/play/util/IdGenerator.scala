package io.flow.play.util

object IdGenerator {

  case object Carrier extends IdGenerator { override val prefix = "car" }
  case object Center extends IdGenerator { override val prefix = "ctr" }
  case object Region extends IdGenerator { override val prefix = "reg" }
  case object Ruleset extends IdGenerator { override val prefix = "rls" }
  case object ServiceLevel extends IdGenerator { override val prefix = "svl" }

  private[util] val Separator = "-"
  private[IdGenerator] val DefaultLength = 20

}

/**
  * Unique set of prefixes we use when generated objects. This is here
  * to centralize the allocation of prefixes used for unique
  * identifiers to avoid conflicts.
  */
sealed trait IdGenerator {

  private[this] val random = new Random()

  def prefix(): String

  def randomId(): String = {
    prefix.toString + IdGenerator.Separator + random.alphaNumeric(randomLength)
  }

  def length(): Int = IdGenerator.DefaultLength

  private[util] def randomLength = {
    length - prefix().length - IdGenerator.Separator.length
  }

}
