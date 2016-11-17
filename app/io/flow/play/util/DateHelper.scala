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

}

/**
  * Default date helpers assume eastern time.
  */
object DateHelper extends DateFormats {

  val EasternTimezone = DateTimeZone.forID("America/New_York")
  private[this] val Default = DateHelper(EasternTimezone)

  override def mmmDdYyyy(dateTime: DateTime) = Default.mmmDdYyyy(dateTime)

  override def shortDate(dateTime: DateTime) = Default.shortDate(dateTime)

  override def shortDateTime(dateTime: DateTime) = Default.shortDateTime(dateTime)

  override def longDate(dateTime: DateTime) = Default.longDate(dateTime)

  override def longDateTime(dateTime: DateTime) = Default.longDateTime(dateTime)

  override def consoleLongDateTime(dateTime: DateTime) = Default.consoleLongDateTime(dateTime)


  /**
    * Turns "1" into "01", leaves "12" as "12"
    */
  def prefixZero(value: Int): String = {
    (value < 10) match {
      case true => s"0$value"
      case false => value.toString
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

}
