package io.flow.play.util

import org.joda.time.{DateTime, DateTimeZone}

object IdGenerator {

  val DefaultTimeZone = DateTimeZone.forID("America/New_York")
  val PrefixLength = 3
  val Separator = "-"
}

/**
  * Generates a new unique ID for a resource. These IDs are a bit
  * simpler for a human to understand / type and are preferred when we
  * expect humans to interact w/ the IDs.
  * 
  * @param prefix Global prefix to identify the type of resource for which you
  *         are generating an ID. Must be 3 characters, lowercase.
  * @param timezone The ID includes a date stamp (e.g. 20150405) - the timezone
  *         name is used to figure out what day it is. Defaults to America/New_York
  */
case class IdGenerator(
  prefix: String,
  timezone: DateTimeZone = IdGenerator.DefaultTimeZone
) {
  assert(prefix.toLowerCase == prefix, s"prefix[$prefix] must be in lower case")
  assert(prefix.trim == prefix, s"prefix[$prefix] must be trimmed")
  assert(prefix.length == IdGenerator.PrefixLength, s"prefix[$prefix] must be ${IdGenerator.PrefixLength} characters long")

  private[this] val random = new Random()
  private[this] val idFormat = Seq("%s", "%s", "%s").mkString(IdGenerator.Separator)

  def randomId(): String = {
    idFormat.format(prefix, dateString(), random.positiveInt())
  }

  /**
    * Returns the current date as a string like
    * "20150919"
    */
  def dateString(): String = {
    val now = (new DateTime()).toDateTime(timezone)
    s"${now.getYear}${prefixZero(now.getMonthOfYear)}${prefixZero(now.getDayOfMonth)}"
  }

  private[this] def prefixZero(value: Int): String = {
    (value < 10) match {
      case true => s"0$value"
      case false => value.toString
    }
  }

}
