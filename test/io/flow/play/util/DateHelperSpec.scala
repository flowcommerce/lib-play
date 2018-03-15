package io.flow.play.util

import org.joda.time.format.ISODateTimeFormat.dateTimeParser
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{MustMatchers, WordSpec}

class DateHelperSpec extends WordSpec with MustMatchers {

  private[this] val jan1 = dateTimeParser.parseDateTime("2016-01-01T08:26:18.794-05:00")
  

  "yyyymm" in {
    DateHelper(DateHelper.EasternTimezone).yyyymm(jan1) must equal("201601")
  }

  "yyyymmdd" in {
    DateHelper(DateHelper.EasternTimezone).yyyymmdd(jan1) must equal("20160101")
  }

  "mmmDdYyyy" in {
    DateHelper(DateHelper.EasternTimezone).mmmDdYyyy must equal("Jan 1, 2016")
    DateHelper(DateHelper.EasternTimezone).mmmDdYyyy(Some(jan1), "-") must equal("Jan 1, 2016")
    DateHelper(DateHelper.EasternTimezone).mmmDdYyyy(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).mmmDdYyyy(None, "-") must equal("-")
  }

  "shortDate" in {
    DateHelper(DateHelper.EasternTimezone).shortDate must equal("1/1/16")
    DateHelper(DateHelper.EasternTimezone).shortDate(Some(jan1), "-") must equal("1/1/16")
    DateHelper(DateHelper.EasternTimezone).shortDate(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).shortDate(None, "-") must equal("-")
  }

  "shortDateTime" in {
    DateHelper(DateHelper.EasternTimezone).shortDateTime must equal("1/1/16 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).shortDateTime(Some(jan1), "-") must equal("1/1/16 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).shortDateTime(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).shortDateTime(None, "-") must equal("-")
  }

  "longDate" in {
    DateHelper(DateHelper.EasternTimezone).longDate must equal("January 1, 2016")
    DateHelper(DateHelper.EasternTimezone).longDate(Some(jan1), "-") must equal("January 1, 2016")
    DateHelper(DateHelper.EasternTimezone).longDate(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).longDate(None, "-") must equal("-")
  }

  "longDateTime" in {
    DateHelper(DateHelper.EasternTimezone).longDateTime must equal("January 1, 2016 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).longDateTime(Some(jan1), "-") must equal("January 1, 2016 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).longDateTime(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).longDateTime(None, "-") must equal("-")
  }

  "consoleLongDateTime" in {
    DateHelper(DateHelper.EasternTimezone).consoleLongDateTime must equal("2016-01-01 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).consoleLongDateTime(Some(jan1), "-") must equal("2016-01-01 08:26:18 EST")
    DateHelper(DateHelper.EasternTimezone).consoleLongDateTime(None) must equal("N/A")
    DateHelper(DateHelper.EasternTimezone).consoleLongDateTime(None, "-") must equal("-")
  }

  "currentYear" in {
    DateHelper(DateHelper.EasternTimezone).currentYear >= 2016
    DateHelper(DateHelper.EasternTimezone).currentYear <= DateTime.now.getYear + 1
  }

  "copyrightYear" in {
    val value = DateHelper(DateHelper.EasternTimezone).copyrightYears
    Seq("2016", s"2016 - ${DateHelper(DateHelper.EasternTimezone).currentYear}").contains(value) mustBe(true)
  }

  "filenameDateTime" in {
    val dtNy = jan1.withZone(DateTimeZone.forID("America/New_York"))
    val dtUtc = jan1.withZone(DateTimeZone.UTC)
    DateHelper(dtNy).filenameDateTime mustBe "20160101.082618.794"
    DateHelper(dtUtc).filenameDateTime mustBe "20160101.132618.794"
  }

  "implicit ordering" in {
    import DateHelper(DateHelper.EasternTimezone)._
    val now = DateTime.now
    val nowPlus1 = now.plusMinutes(1)
    val nowPlus5 = now.plusMinutes(5)
    val nowPlus10 = now.plusMinutes(10)

    val datetimes = Seq(nowPlus10, nowPlus5, nowPlus1, now)

    datetimes.sorted must equal(datetimes.reverse)
  }

}
