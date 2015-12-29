package io.flow.play.util

/**
  * Unique set of prefixes we use when generated objects. This is here
  * to centralize the allocation of prefixes used for unique
  * identifiers to avoid conflicts.
  */
sealed trait Prefix

object Prefix {

  case object Carrier extends Prefix { override def toString() = "car" }
  case object Center extends Prefix { override def toString() = "ctr" }
  case object Region extends Prefix { override def toString() = "reg" }
  case object Ruleset extends Prefix { override def toString() = "rls" }
  case object ServiceLevel extends Prefix { override def toString() = "svl" }

}

object IdGenerator {

  val Separator = "-"

}

case class IdGenerator(prefix: Prefix) {

  private[this] val random = new Random()

  def randomId(): String = {
    prefix.toString + IdGenerator.Separator + random.positiveLong()
  }
}

