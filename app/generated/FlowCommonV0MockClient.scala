/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.2.50
 * apidoc:0.11.52 http://www.apidoc.me/flow/common/0.2.50/play_2_4_mock_client
 */
package io.flow.common.v0.mock {

  object Factories {

    def randomString(): String = {
      "Test " + _root_.java.util.UUID.randomUUID.toString.replaceAll("-", " ")
    }

    def makeCalendar() = io.flow.common.v0.models.Calendar.Weekdays

    def makeCapability() = io.flow.common.v0.models.Capability.Crossdock

    def makeChangeType() = io.flow.common.v0.models.ChangeType.Insert

    def makeDeliveredDuty() = io.flow.common.v0.models.DeliveredDuty.Paid

    def makeEnvironment() = io.flow.common.v0.models.Environment.Sandbox

    def makeExceptionType() = io.flow.common.v0.models.ExceptionType.Open

    def makeHolidayCalendar() = io.flow.common.v0.models.HolidayCalendar.UsBankHolidays

    def makeMarginType() = io.flow.common.v0.models.MarginType.Fixed

    def makeMeasurementSystem() = io.flow.common.v0.models.MeasurementSystem.Imperial

    def makeRole() = io.flow.common.v0.models.Role.Admin

    def makeRoundingMethod() = io.flow.common.v0.models.RoundingMethod.Up

    def makeRoundingType() = io.flow.common.v0.models.RoundingType.Pattern

    def makeScheduleExceptionStatus() = io.flow.common.v0.models.ScheduleExceptionStatus.Open

    def makeSortDirection() = io.flow.common.v0.models.SortDirection.Ascending

    def makeUnitOfMeasurement() = io.flow.common.v0.models.UnitOfMeasurement.Millimeter

    def makeUnitOfTime() = io.flow.common.v0.models.UnitOfTime.Year

    def makeUserStatus() = io.flow.common.v0.models.UserStatus.Pending

    def makeValueAddedService() = io.flow.common.v0.models.ValueAddedService.HazardousMaterial

    def makeVisibility() = io.flow.common.v0.models.Visibility.Public

    def makeAddress() = io.flow.common.v0.models.Address(
      text = None,
      streets = None,
      city = None,
      province = None,
      postal = None,
      country = None,
      latitude = None,
      longitude = None
    )

    def makeContact() = io.flow.common.v0.models.Contact(
      name = io.flow.common.v0.mock.Factories.makeName(),
      company = None,
      email = None,
      phone = None
    )

    def makeCustomer() = io.flow.common.v0.models.Customer(
      name = io.flow.common.v0.mock.Factories.makeName(),
      number = None,
      phone = None,
      email = None
    )

    def makeDatetimeRange() = io.flow.common.v0.models.DatetimeRange(
      from = new org.joda.time.DateTime(),
      to = new org.joda.time.DateTime()
    )

    def makeDimension() = io.flow.common.v0.models.Dimension(
      depth = None,
      diameter = None,
      length = None,
      weight = None,
      width = None
    )

    def makeDimensions() = io.flow.common.v0.models.Dimensions(
      product = None,
      packaging = None
    )

    def makeException() = io.flow.common.v0.models.Exception(
      `type` = io.flow.common.v0.mock.Factories.makeExceptionType(),
      datetimeRange = io.flow.common.v0.mock.Factories.makeDatetimeRange()
    )

    def makeExperienceSummary() = io.flow.common.v0.models.ExperienceSummary(
      id = randomString(),
      key = randomString(),
      name = randomString()
    )

    def makeLineItem() = io.flow.common.v0.models.LineItem(
      number = randomString(),
      quantity = 1l,
      price = io.flow.common.v0.mock.Factories.makeMoney(),
      attributes = Map(),
      center = None,
      discount = None
    )

    def makeLineItemForm() = io.flow.common.v0.models.LineItemForm(
      number = randomString(),
      quantity = 1l,
      price = None,
      attributes = None,
      center = None,
      discount = None
    )

    def makeMargin() = io.flow.common.v0.models.Margin(
      `type` = io.flow.common.v0.mock.Factories.makeMarginType(),
      value = BigDecimal("1")
    )

    def makeMeasurement() = io.flow.common.v0.models.Measurement(
      value = randomString(),
      units = io.flow.common.v0.mock.Factories.makeUnitOfMeasurement()
    )

    def makeMoney() = io.flow.common.v0.models.Money(
      amount = 1.0,
      currency = randomString()
    )

    def makeName() = io.flow.common.v0.models.Name(
      first = None,
      last = None
    )

    def makeOrganization() = io.flow.common.v0.models.Organization(
      id = randomString(),
      name = randomString(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      parent = None
    )

    def makeOrganizationReference() = io.flow.common.v0.models.OrganizationReference(
      id = randomString()
    )

    def makeOrganizationSummary() = io.flow.common.v0.models.OrganizationSummary(
      id = randomString(),
      name = randomString()
    )

    def makePrice() = io.flow.common.v0.models.Price(
      amount = 1.0,
      currency = randomString(),
      label = randomString()
    )

    def makePriceForm() = io.flow.common.v0.models.PriceForm(
      amount = 1.0,
      currency = randomString()
    )

    def makePriceWithBase() = io.flow.common.v0.models.PriceWithBase(
      currency = randomString(),
      amount = 1.0,
      label = randomString(),
      base = None
    )

    def makeRounding() = io.flow.common.v0.models.Rounding(
      `type` = io.flow.common.v0.mock.Factories.makeRoundingType(),
      method = io.flow.common.v0.mock.Factories.makeRoundingMethod(),
      value = BigDecimal("1")
    )

    def makeSchedule() = io.flow.common.v0.models.Schedule(
      calendar = None,
      holiday = io.flow.common.v0.mock.Factories.makeHolidayCalendar(),
      exception = Nil,
      cutoff = None,
      minLeadTime = None,
      maxLeadTime = None
    )

    def makeUser() = io.flow.common.v0.models.User(
      id = randomString(),
      email = None,
      name = io.flow.common.v0.mock.Factories.makeName(),
      status = io.flow.common.v0.mock.Factories.makeUserStatus()
    )

    def makeUserReference() = io.flow.common.v0.models.UserReference(
      id = randomString()
    )

    def makeZone() = io.flow.common.v0.models.Zone(
      province = None,
      country = randomString()
    )

    def makeExpandableOrganization() = io.flow.common.v0.mock.Factories.makeOrganization()

    def makeExpandableUser() = io.flow.common.v0.mock.Factories.makeUser()

  }

}