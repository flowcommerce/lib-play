package io.flow.play.util

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

/**
  * Standardized date formats used across Flow
  */
trait DateFormats {

  def mmmDdYyyy(dateTime: DateTime): String
  def mmmDdYyyy(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(mmmDdYyyy(_)).getOrElse(default)
  }

  def shortDate(dateTime: DateTime): String
  def shortDate(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(shortDate(_)).getOrElse(default)
  }

  def shortDateTime(dateTime: DateTime): String
  def shortDateTime(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(shortDateTime(_)).getOrElse(default)
  }

  def longDate(dateTime: DateTime): String
  def longDate(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(longDate(_)).getOrElse(default)
  }

  def longDateTime(dateTime: DateTime): String
  def longDateTime(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(longDateTime(_)).getOrElse(default)
  }

  def consoleLongDateTime(dateTime: DateTime): String
  def consoleLongDateTime(dateTime: Option[DateTime], default: String = "N/A"): String = {
    dateTime.map(consoleLongDateTime(_)).getOrElse(default)
  }

  def filenameDateTime(dateTime: DateTime): String
  def filenameDateTime(dateTime: Option[DateTime], default: String = "N/A"): String =
    dateTime.map(filenameDateTime).getOrElse(default)

}

/**
  * Default date helpers assume eastern time.
  */
object DateHelper extends DateFormats {

  /** This implicit ordering allows us to called `.sorted` on a Seq[DateTime]. */
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  val CopyrightStartYear = 2016

  val FilenameDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd.HHmmss.SSS")

  val EasternTimezone = DateTimeZone.forID("America/New_York")

  def apply(dateTime: DateTime): DateHelper = {
    DateHelper(dateTime.getZone)
  }
  
  override def mmmDdYyyy(dateTime: DateTime) = DateHelper(dateTime).mmmDdYyyy(dateTime)

  override def shortDate(dateTime: DateTime) = DateHelper(dateTime).shortDate(dateTime)

  override def shortDateTime(dateTime: DateTime) = DateHelper(dateTime).shortDateTime(dateTime)

  override def longDate(dateTime: DateTime) = DateHelper(dateTime).longDate(dateTime)

  override def longDateTime(dateTime: DateTime) = DateHelper(dateTime).longDateTime(dateTime)

  override def consoleLongDateTime(dateTime: DateTime) = DateHelper(dateTime).consoleLongDateTime(dateTime)

  /**
    * Returns a filename friendly datetime string
    * The returned string respects the timezone of the datetime and is not part of the string itself
    */
  override def filenameDateTime(dateTime: DateTime): String = DateHelper(dateTime).filenameDateTime(dateTime)

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
    * Returns the specified date (defaults to now) as a string like
    * "20150919"
    */
  def yyyymmdd(
    dateTime: DateTime = new DateTime(),
    zone: DateTimeZone = EasternTimezone
  ): String = {
    yyyymm(dateTime, zone) + prefixZero(dateTime.getDayOfMonth)
  }

  /**
    * Returns the specified date (defaults to now) as a string like
    * "201509"
    */
  def yyyymm(
    dateTime: DateTime = new DateTime(),
    zone: DateTimeZone = EasternTimezone
  ): String = {
    s"${dateTime.getYear}${prefixZero(dateTime.getMonthOfYear)}"
  }

  /**
    * Returns the current year (e.g. 2016) in the default timezone
    */
  def currentYear: Int = {
    (new DateTime()).withZone(EasternTimezone).getYear
  }

  /**
    * Returns either '2016' or '2016 - 2017' intended to be used for displaying things
    * like the Flow copy right years dynamically.
    */
  def copyrightYears: String = {
    val current = currentYear
    current > CopyrightStartYear match {
      case true => s"$CopyrightStartYear - $current"
      case false => CopyrightStartYear.toString
    }
  }

}

case class DateHelper(
  timezone: DateTimeZone
) extends DateFormats {

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

  override def filenameDateTime(dateTime: DateTime): String =
    DateHelper.FilenameDateTimeFormatter.withZone(timezone).print(dateTime)

}
