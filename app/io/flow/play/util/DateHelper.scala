package io.flow.delta.www.lib
//package io.flow.play.util

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

/**
  * Helpers for formatting dates for human display
  */
object DateHelper {

  private[this] val EasternTime = DateTimeZone.forID("America/New_York")

  private[this] val DefaultLabel = "N/A"

  def shortDate(
    dateTime: DateTime
  ): String = shortDateOption(Some(dateTime))

  def shortDateOption(
    dateTime: Option[DateTime],
    default: String = DefaultLabel
  ): String = {
    dateTime match {
      case None => default
      case Some(dt) => {
        DateTimeFormat.shortDate.withZone(EasternTime).print(dt)
      }
    }
  }

  def shortDateTime(
    dateTime: DateTime
  ): String = shortDateTimeOption(Some(dateTime))

  def shortDateTimeOption(
    dateTime: Option[DateTime],
    default: String = DefaultLabel
  ): String = {
    dateTime match {
      case None => default
      case Some(dt) => {
        DateTimeFormat.forPattern("MM/dd/YY HH:mm:ss z").withZone(EasternTime).print(dt)
      }
    }
  }
  
  def longDateTime(
    dateTime: DateTime
  ): String = longDateTimeOption(Some(dateTime))

  def longDateTimeOption(
    dateTime: Option[DateTime],
    default: String = DefaultLabel
  ): String = {
    dateTime match {
      case None => default
      case Some(dt) => {
        DateTimeFormat.longDateTime.withZone(EasternTime).print(dt)
      }
    }
  }

  def consoleLongDateTime(
    dateTime: DateTime
  ): String = consoleLongDateTimeOption(Some(dateTime))

  def consoleLongDateTimeOption(
    dateTime: Option[DateTime],
    default: String = DefaultLabel
  ): String = {
    dateTime match {
      case None => default
      case Some(dt) => {
        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z").withZone(EasternTime).print(dt)
      }
    }
  }
  
}
