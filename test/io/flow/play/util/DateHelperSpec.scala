package io.flow.play.util

import org.joda.time.format.ISODateTimeFormat.dateTimeParser
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{MustMatchers, WordSpec}

class DateHelperSpec extends WordSpec with MustMatchers {

  private[this] val jan1 = dateTimeParser.parseDateTime("2016-01-01T08:26:18.794-05:00")
  private[this] val jan1Eastern = jan1.withZone(DateHelper.EasternTimezone)
  

  "yyyymm" in {
    DateHelper(jan1Eastern).yyyymm() must equal("201601")
  }

  "yyyymmdd" in {
    DateHelper(jan1Eastern).yyyymmdd() must equal("20160101")
  }

  "mmmDdYyyy" in {
    DateHelper(jan1Eastern).mmmDdYyyy() must equal("Jan 1, 2016")
  }

  "shortDate" in {
    DateHelper(jan1Eastern).shortDate() must equal("1/1/16")
  }

  "shortDateTime" in {
    DateHelper(jan1Eastern).shortDateTime() must equal("1/1/16 08:26:18 EST")
  }

  "longDate" in {
    DateHelper(jan1Eastern).longDate() must equal("January 1, 2016")
  }

  "longDateTime" in {
    DateHelper(jan1Eastern).longDateTime() must equal("January 1, 2016 08:26:18 EST")
  }

  "consoleLongDateTime" in {
    DateHelper(jan1Eastern).consoleLongDateTime() must equal("2016-01-01 08:26:18 EST")
  }

  "currentYear" in {
    DateHelper.currentYear >= 2016
    DateHelper.currentYear <= DateTime.now.getYear + 1
  }

  "copyrightYear" in {
    val value = DateHelper.copyrightYears
    Seq("2016", s"2016 - ${DateHelper.currentYear}").contains(value) mustBe(true)
  }

  "filenameDateTime" in {
    DateHelper(jan1.withZone(DateTimeZone.forID("America/New_York"))).filenameDateTime() mustBe "20160101.082618.794"
    DateHelper(jan1.withZone(DateTimeZone.UTC)).filenameDateTime() mustBe "20160101.132618.794"
  }

  "implicit ordering" in {
    import DateHelper.Implicits._

    val now = DateTime.now
    val nowPlus1 = now.plusMinutes(1)
    val nowPlus5 = now.plusMinutes(5)
    val nowPlus10 = now.plusMinutes(10)

    val datetimes = Seq(nowPlus10, nowPlus5, nowPlus1, now)

    datetimes.sorted must equal(datetimes.reverse)
  }

}
