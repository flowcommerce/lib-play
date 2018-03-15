package io.flow.play.util

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateHelper {

  /** This implicit ordering allows us to called `.sorted` on a Seq[DateTime]. */
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  val CopyrightStartYear:  Int = 2016

  val EasternTimezone: DateTimeZone = DateTimeZone.forID("America/New_York")

  /**
    * Turns "1" into "01", leaves "12" as "12"
    */
  def prefixZero(value: Int): String = {
    if (value > 0 && value < 10) {
      s"0$value"
    } else {
      value.toString
    }
  }

  def trimLeadingZero(value: String): String = {
    value.stripPrefix("0")
  }

  /**
    * Returns the current year (e.g. 2016) in the eastern timezone
    */
  def currentYear: Int = {
    DateTime.now.withZone(EasternTimezone).getYear
  }

  /**
    * Returns either '2016' or '2016 - 2018' intended to be used for displaying things
    * like the Flow copyright years dynamically.
    */
  def copyrightYears: String = {
    val current = currentYear
    if (current > CopyrightStartYear) {
      s"$CopyrightStartYear - $current"
    } else {
      CopyrightStartYear.toString
    }
  }
}

case class DateHelper(
  dateTime: DateTime
) {

  private[this] val filenameDateTimeFormatter: DateTimeFormatter = DateTimeFormat.
    forPattern("yyyyMMdd.HHmmss.SSS")

  private[this] val TimeFormat: DateTimeFormatter = DateTimeFormat.
    forPattern("HH:mm:ss z")

  def mmmDdYyyy(): String = {
    DateTimeFormat.forPattern("MMM").print(dateTime) + " " +
      DateHelper.trimLeadingZero(DateTimeFormat.forPattern("dd").print(dateTime)) + ", " +
      DateTimeFormat.forPattern("YYYY").print(dateTime)
  }

  def shortDate(): String = {
    DateTimeFormat.shortDate.print(dateTime)
  }

  def shortDateTime(): String = {
    shortDate() + " " + TimeFormat.print(dateTime)
  }

  def longDate(): String = {
    DateTimeFormat.forPattern("MMMM").print(dateTime) + " " +
      DateHelper.trimLeadingZero(DateTimeFormat.forPattern("dd").print(dateTime)) + ", " +
      DateTimeFormat.forPattern("YYYY").print(dateTime)
  }

  def longDateTime(): String = {
    longDate() + " " + TimeFormat.print(dateTime)
  }

  def consoleLongDateTime(): String = {
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z").print(dateTime)
  }

  def filenameDateTime(): String = {
    filenameDateTimeFormatter.print(dateTime)
  }

  /**
    * Returns the specified date (defaults to now) as a string like
    * "201509"
    */
  def yyyymm(): String = {
    s"${dateTime.getYear}${DateHelper.prefixZero(dateTime.getMonthOfYear)}"
  }

  /**
    * Returns the specified date (defaults to now) as a string like
    * "201509"
    */
  def yyyymmdd(): String = {
    yyyymm() + DateHelper.prefixZero(dateTime.getDayOfMonth)
  }

}
