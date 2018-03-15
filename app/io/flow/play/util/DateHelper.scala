package io.flow.play.util

import io.flow.play.util.DateHelper.{CopyrightStartYear, EasternTimezone}
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateHelper {

  def apply(dateTime: DateTime): DateHelper = DateHelper(dateTime.getZone)

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
}

case class DateHelper(
  timezone: DateTimeZone
) {

  private[this] val filenameDateTimeFormatter: DateTimeFormatter = DateTimeFormat.
    forPattern("yyyyMMdd.HHmmss.SSS").
    withZone(timezone)

  def mmmDdYyyy(dateTime: DateTime): String = {
    DateTimeFormat.forPattern("MMM").withZone(timezone).print(dateTime) + " " +
    DateHelper.trimLeadingZero(DateTimeFormat.forPattern("dd").withZone(timezone).print(dateTime)) + ", " +
    DateTimeFormat.forPattern("YYYY").withZone(timezone).print(dateTime)
  }

  def shortDate(
    dateTime: DateTime
  ): String = {
    DateTimeFormat.shortDate.withZone(timezone).print(dateTime)
  }

  def shortDateTime(
    dateTime: DateTime
  ): String = {
    shortDate(dateTime) + " " + DateTimeFormat.forPattern("HH:mm:ss z").withZone(timezone).print(dateTime)
  }
  
  def longDate(
    dateTime: DateTime
  ): String = {
    DateTimeFormat.forPattern("MMMM").withZone(timezone).print(dateTime) + " " +
    DateHelper.trimLeadingZero(DateTimeFormat.forPattern("dd").withZone(timezone).print(dateTime)) + ", " +
    DateTimeFormat.forPattern("YYYY").withZone(timezone).print(dateTime)
  }

  def longDateTime(
    dateTime: DateTime
  ): String = {
    longDate(dateTime) + " " + DateTimeFormat.forPattern("HH:mm:ss z").withZone(timezone).print(dateTime)
  }

  def consoleLongDateTime(
    dateTime: DateTime
  ): String = {
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z").withZone(timezone).print(dateTime)
  }

  def filenameDateTime(dateTime: DateTime): String = {
    filenameDateTimeFormatter.print(dateTime)
  }

  /**
    * Returns the current year (e.g. 2016) in the default timezone
    */
  def currentYear: Int = {
    DateTime.now.withZone(EasternTimezone).getYear
  }

  /**
    * Returns either '2016' or '2016 - 2017' intended to be used for displaying things
    * like the Flow copy right years dynamically.
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
