package io.flow.play.util

import org.joda.time.{DateTime, DateTimeZone}

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
  private[this] val tz = DateTimeZone.forID(timezoneName())

  def prefix(): String

  def randomId(): String = {
    prefix.toString + IdGenerator.Separator + dateString() + IdGenerator.Separator + random.alphaNumeric(randomLength)
    prefix.toString + IdGenerator.Separator + dateString() + IdGenerator.Separator + random.positiveInt()
  }

  def length(): Int = IdGenerator.DefaultLength

  def timezoneName(): String = "America/New_York"

  private[this] def prefixZero(value: Int): String = {
    (value < 10) match {
      case true => s"0$value"
      case false => value.toString
    }
  }

  /**
    * Returns the current date as a string like
    * "20150919"
    */
  def dateString(): String = {
    val now = (new DateTime()).toDateTime(tz)
    s"${now.getYear}${prefixZero(now.getMonthOfYear)}${prefixZero(now.getDayOfMonth)}"
  }

  private[util] def randomLength = {
    length - prefix().length - IdGenerator.Separator.length
  }

}
