package io.flow.play.util

import org.joda.time.format.ISODateTimeFormat.dateTimeParser
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{MustMatchers, WordSpec}

class DateHelperSpec extends WordSpec with MustMatchers {

  private[this] val jan1 = dateTimeParser.
    parseDateTime("2016-01-01T08:26:18.794-05:00").
    withZone(DateHelper.EasternTimezone)

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

  "currentYear" in {
    DateHelper.currentYear >= 2016
    DateHelper.currentYear <= (new DateTime()).getYear + 1
  }

  "copyrightYear" in {
    val value = DateHelper.copyrightYears
    Seq("2016", s"2016 - ${DateHelper.currentYear}").contains(value) mustBe(true)
  }

  "filenameDateTime" in {
    val dtNy = jan1.withZone(DateTimeZone.forID("America/New_York"))
    val dtUtc = jan1.withZone(DateTimeZone.UTC)
    DateHelper.filenameDateTime(dtNy) mustBe "20160101.082618.794"
    DateHelper.filenameDateTime(dtUtc) mustBe "20160101.132618.794"

    DateHelper.filenameDateTime(Some(jan1.withZone(DateTimeZone.forID("America/New_York"))), "-") mustBe "20160101.082618.794"
    DateHelper.filenameDateTime(None) mustBe "N/A"
    DateHelper.filenameDateTime(None, "-") mustBe "-"
  }

  "implicit ordering" in {
    import DateHelper._
    val now = DateTime.now
    val nowPlus1 = now.plusMinutes(1)
    val nowPlus5 = now.plusMinutes(5)
    val nowPlus10 = now.plusMinutes(10)

    val datetimes = Seq(nowPlus10, nowPlus5, nowPlus1, now)

    datetimes.sorted must equal(datetimes.reverse)
  }

}
