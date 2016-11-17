package io.flow.play.util

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat.dateTimeParser
import org.scalatestplus.play._

class DateHelperSpec extends PlaySpec with OneAppPerSuite {

  val jan1 = dateTimeParser.parseDateTime("2016-01-01T08:26:18.794-05:00")

  "yyyymm" in {
    DateHelper.yyyymm(jan1) must equal("201601")
  }

  "yyyymmdd" in {
    DateHelper.yyyymmdd(jan1) must equal("20160101")
  }

  "mmmDdYyyy" in {
    DateHelper.mmmDdYyyy(jan1) must equal("Jan 1, 2016")
    DateHelper.mmmDdYyyy(Some(jan1), "-") must equal("Jan 1, 2016")
    DateHelper.mmmDdYyyy(None) must equal("N/A")
    DateHelper.mmmDdYyyy(None, "-") must equal("-")
  }

  "shortDate" in {
    DateHelper.shortDate(jan1) must equal("1/1/16")
    DateHelper.shortDate(Some(jan1), "-") must equal("1/1/16")
    DateHelper.shortDate(None) must equal("N/A")
    DateHelper.shortDate(None, "-") must equal("-")
  }

  "shortDateTime" in {
    DateHelper.shortDateTime(jan1) must equal("1/1/16 08:26:18 EST")
    DateHelper.shortDateTime(Some(jan1), "-") must equal("1/1/16 08:26:18 EST")
    DateHelper.shortDateTime(None) must equal("N/A")
    DateHelper.shortDateTime(None, "-") must equal("-")
  }

  "longDate" in {
    DateHelper.longDate(jan1) must equal("January 1, 2016")
    DateHelper.longDate(Some(jan1), "-") must equal("January 1, 2016")
    DateHelper.longDate(None) must equal("N/A")
    DateHelper.longDate(None, "-") must equal("-")
  }

  "longDateTime" in {
    DateHelper.longDateTime(jan1) must equal("January 1, 2016 08:26:18 EST")
    DateHelper.longDateTime(Some(jan1), "-") must equal("January 1, 2016 08:26:18 EST")
    DateHelper.longDateTime(None) must equal("N/A")
    DateHelper.longDateTime(None, "-") must equal("-")
  }

  "consoleLongDateTime" in {
    DateHelper.consoleLongDateTime(jan1) must equal("2016-01-01 08:26:18 EST")
    DateHelper.consoleLongDateTime(Some(jan1), "-") must equal("2016-01-01 08:26:18 EST")
    DateHelper.consoleLongDateTime(None) must equal("N/A")
    DateHelper.consoleLongDateTime(None, "-") must equal("-")
  }

}
