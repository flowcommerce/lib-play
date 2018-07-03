/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.5.76
 * apibuilder 0.14.3 app.apibuilder.io/flow/common/0.5.76/play_2_6_mock_client
 */
package io.flow.common.v0.mock {

  object Factories {

    def randomString(): String = {
      "Test " + _root_.java.util.UUID.randomUUID.toString.replaceAll("-", " ")
    }

    def makeAttributeDataType(): io.flow.common.v0.models.AttributeDataType = io.flow.common.v0.models.AttributeDataType.Boolean

    def makeAvailabilityStatus(): io.flow.common.v0.models.AvailabilityStatus = io.flow.common.v0.models.AvailabilityStatus.Enabled

    def makeCalendar(): io.flow.common.v0.models.Calendar = io.flow.common.v0.models.Calendar.Weekdays

    def makeCapability(): io.flow.common.v0.models.Capability = io.flow.common.v0.models.Capability.Crossdock

    def makeChangeType(): io.flow.common.v0.models.ChangeType = io.flow.common.v0.models.ChangeType.Insert

    def makeCurrencyLabelFormatter(): io.flow.common.v0.models.CurrencyLabelFormatter = io.flow.common.v0.models.CurrencyLabelFormatter.StripTrailingZeros

    def makeCurrencySymbolFormat(): io.flow.common.v0.models.CurrencySymbolFormat = io.flow.common.v0.models.CurrencySymbolFormat.Narrow

    def makeDayOfWeek(): io.flow.common.v0.models.DayOfWeek = io.flow.common.v0.models.DayOfWeek.Sunday

    def makeDeliveredDuty(): io.flow.common.v0.models.DeliveredDuty = io.flow.common.v0.models.DeliveredDuty.Paid

    def makeEnvironment(): io.flow.common.v0.models.Environment = io.flow.common.v0.models.Environment.Sandbox

    def makeExceptionType(): io.flow.common.v0.models.ExceptionType = io.flow.common.v0.models.ExceptionType.Open

    def makeHolidayCalendar(): io.flow.common.v0.models.HolidayCalendar = io.flow.common.v0.models.HolidayCalendar.UsBankHolidays

    def makeIncludedLevyKey(): io.flow.common.v0.models.IncludedLevyKey = io.flow.common.v0.models.IncludedLevyKey.Duty

    def makeIncoterm(): io.flow.common.v0.models.Incoterm = io.flow.common.v0.models.Incoterm.Exw

    def makeMarginType(): io.flow.common.v0.models.MarginType = io.flow.common.v0.models.MarginType.Fixed

    def makeMeasurementSystem(): io.flow.common.v0.models.MeasurementSystem = io.flow.common.v0.models.MeasurementSystem.Imperial

    def makeMerchantOfRecord(): io.flow.common.v0.models.MerchantOfRecord = io.flow.common.v0.models.MerchantOfRecord.Flow

    def makeOrderMerchantOfRecord(): io.flow.common.v0.models.OrderMerchantOfRecord = io.flow.common.v0.models.OrderMerchantOfRecord.Flow

    def makePriceBookStatus(): io.flow.common.v0.models.PriceBookStatus = io.flow.common.v0.models.PriceBookStatus.Draft

    def makeRole(): io.flow.common.v0.models.Role = io.flow.common.v0.models.Role.Admin

    def makeRoundingMethod(): io.flow.common.v0.models.RoundingMethod = io.flow.common.v0.models.RoundingMethod.Up

    def makeRoundingType(): io.flow.common.v0.models.RoundingType = io.flow.common.v0.models.RoundingType.Pattern

    def makeScheduleExceptionStatus(): io.flow.common.v0.models.ScheduleExceptionStatus = io.flow.common.v0.models.ScheduleExceptionStatus.Open

    def makeSortDirection(): io.flow.common.v0.models.SortDirection = io.flow.common.v0.models.SortDirection.Ascending

    def makeUnitOfMeasurement(): io.flow.common.v0.models.UnitOfMeasurement = io.flow.common.v0.models.UnitOfMeasurement.Millimeter

    def makeUnitOfTime(): io.flow.common.v0.models.UnitOfTime = io.flow.common.v0.models.UnitOfTime.Year

    def makeUserStatus(): io.flow.common.v0.models.UserStatus = io.flow.common.v0.models.UserStatus.Pending

    def makeValueAddedService(): io.flow.common.v0.models.ValueAddedService = io.flow.common.v0.models.ValueAddedService.HazardousMaterial

    def makeVisibility(): io.flow.common.v0.models.Visibility = io.flow.common.v0.models.Visibility.Public

    def makeAddress(): io.flow.common.v0.models.Address = io.flow.common.v0.models.Address(
      text = None,
      streets = None,
      streetNumber = None,
      city = None,
      province = None,
      postal = None,
      country = None,
      latitude = None,
      longitude = None
    )

    def makeCatalogItemReference(): io.flow.common.v0.models.CatalogItemReference = io.flow.common.v0.models.CatalogItemReference(
      id = Factories.randomString(),
      number = Factories.randomString()
    )

    def makeCatalogItemSummary(): io.flow.common.v0.models.CatalogItemSummary = io.flow.common.v0.models.CatalogItemSummary(
      number = Factories.randomString(),
      name = Factories.randomString(),
      attributes = Map()
    )

    def makeContact(): io.flow.common.v0.models.Contact = io.flow.common.v0.models.Contact(
      name = io.flow.common.v0.mock.Factories.makeName(),
      company = None,
      email = None,
      phone = None
    )

    def makeCustomer(): io.flow.common.v0.models.Customer = io.flow.common.v0.models.Customer(
      name = io.flow.common.v0.mock.Factories.makeName(),
      number = None,
      phone = None,
      email = None
    )

    def makeDatetimeRange(): io.flow.common.v0.models.DatetimeRange = io.flow.common.v0.models.DatetimeRange(
      from = org.joda.time.DateTime.now,
      to = org.joda.time.DateTime.now
    )

    def makeDimension(): io.flow.common.v0.models.Dimension = io.flow.common.v0.models.Dimension(
      depth = None,
      diameter = None,
      length = None,
      weight = None,
      width = None
    )

    def makeDimensions(): io.flow.common.v0.models.Dimensions = io.flow.common.v0.models.Dimensions(
      product = None,
      packaging = None
    )

    def makeDuration(): io.flow.common.v0.models.Duration = io.flow.common.v0.models.Duration(
      unit = io.flow.common.v0.mock.Factories.makeUnitOfTime(),
      value = 1l
    )

    def makeException(): io.flow.common.v0.models.Exception = io.flow.common.v0.models.Exception(
      `type` = io.flow.common.v0.mock.Factories.makeExceptionType(),
      datetimeRange = io.flow.common.v0.mock.Factories.makeDatetimeRange()
    )

    def makeExperienceSummary(): io.flow.common.v0.models.ExperienceSummary = io.flow.common.v0.models.ExperienceSummary(
      id = Factories.randomString(),
      key = Factories.randomString(),
      name = Factories.randomString(),
      country = None,
      currency = None,
      language = None
    )

    def makeIncludedLevies(): io.flow.common.v0.models.IncludedLevies = io.flow.common.v0.models.IncludedLevies(
      key = io.flow.common.v0.mock.Factories.makeIncludedLevyKey(),
      label = Factories.randomString()
    )

    def makeItemReference(): io.flow.common.v0.models.ItemReference = io.flow.common.v0.models.ItemReference(
      number = Factories.randomString()
    )

    def makeLineItem(): io.flow.common.v0.models.LineItem = io.flow.common.v0.models.LineItem(
      number = Factories.randomString(),
      quantity = 1l,
      price = io.flow.common.v0.mock.Factories.makeMoney(),
      attributes = Map(),
      center = None,
      discount = None
    )

    def makeLineItemForm(): io.flow.common.v0.models.LineItemForm = io.flow.common.v0.models.LineItemForm(
      number = Factories.randomString(),
      quantity = 1l,
      shipmentEstimate = None,
      price = None,
      attributes = None,
      center = None,
      discount = None
    )

    def makeMargin(): io.flow.common.v0.models.Margin = io.flow.common.v0.models.Margin(
      `type` = io.flow.common.v0.mock.Factories.makeMarginType(),
      value = BigDecimal("1")
    )

    def makeMeasurement(): io.flow.common.v0.models.Measurement = io.flow.common.v0.models.Measurement(
      value = Factories.randomString(),
      units = io.flow.common.v0.mock.Factories.makeUnitOfMeasurement()
    )

    def makeMerchantOfRecordEntity(): io.flow.common.v0.models.MerchantOfRecordEntity = io.flow.common.v0.models.MerchantOfRecordEntity(
      name = Factories.randomString(),
      vat = io.flow.common.v0.mock.Factories.makeMerchantOfRecordEntityRegistration(),
      streets = Nil,
      city = Factories.randomString(),
      province = None,
      postal = None,
      country = Factories.randomString(),
      phone = None,
      email = None
    )

    def makeMerchantOfRecordEntityRegistration(): io.flow.common.v0.models.MerchantOfRecordEntityRegistration = io.flow.common.v0.models.MerchantOfRecordEntityRegistration(
      number = Factories.randomString(),
      country = Factories.randomString()
    )

    def makeMoney(): io.flow.common.v0.models.Money = io.flow.common.v0.models.Money(
      amount = 1.0,
      currency = Factories.randomString()
    )

    def makeName(): io.flow.common.v0.models.Name = io.flow.common.v0.models.Name(
      first = None,
      last = None
    )

    def makeOrganization(): io.flow.common.v0.models.Organization = io.flow.common.v0.models.Organization(
      id = Factories.randomString(),
      name = Factories.randomString(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      parent = None
    )

    def makeOrganizationReference(): io.flow.common.v0.models.OrganizationReference = io.flow.common.v0.models.OrganizationReference(
      id = Factories.randomString()
    )

    def makeOrganizationSummary(): io.flow.common.v0.models.OrganizationSummary = io.flow.common.v0.models.OrganizationSummary(
      id = Factories.randomString(),
      name = Factories.randomString()
    )

    def makePartnerReference(): io.flow.common.v0.models.PartnerReference = io.flow.common.v0.models.PartnerReference(
      id = Factories.randomString()
    )

    def makePrice(): io.flow.common.v0.models.Price = io.flow.common.v0.models.Price(
      amount = 1.0,
      currency = Factories.randomString(),
      label = Factories.randomString()
    )

    def makePriceForm(): io.flow.common.v0.models.PriceForm = io.flow.common.v0.models.PriceForm(
      amount = 1.0,
      currency = Factories.randomString()
    )

    def makePriceWithBase(): io.flow.common.v0.models.PriceWithBase = io.flow.common.v0.models.PriceWithBase(
      currency = Factories.randomString(),
      amount = 1.0,
      label = Factories.randomString(),
      base = None
    )

    def makeRounding(): io.flow.common.v0.models.Rounding = io.flow.common.v0.models.Rounding(
      `type` = io.flow.common.v0.mock.Factories.makeRoundingType(),
      method = io.flow.common.v0.mock.Factories.makeRoundingMethod(),
      value = BigDecimal("1")
    )

    def makeSchedule(): io.flow.common.v0.models.Schedule = io.flow.common.v0.models.Schedule(
      calendar = None,
      holiday = io.flow.common.v0.mock.Factories.makeHolidayCalendar(),
      exception = Nil,
      cutoff = None,
      minLeadTime = None,
      maxLeadTime = None
    )

    def makeUser(): io.flow.common.v0.models.User = io.flow.common.v0.models.User(
      id = Factories.randomString(),
      email = None,
      name = io.flow.common.v0.mock.Factories.makeName(),
      status = io.flow.common.v0.mock.Factories.makeUserStatus()
    )

    def makeUserReference(): io.flow.common.v0.models.UserReference = io.flow.common.v0.models.UserReference(
      id = Factories.randomString()
    )

    def makeZone(): io.flow.common.v0.models.Zone = io.flow.common.v0.models.Zone(
      province = None,
      country = Factories.randomString()
    )

    def makeExpandableOrganization(): io.flow.common.v0.models.ExpandableOrganization = io.flow.common.v0.mock.Factories.makeOrganization()

    def makeExpandableUser(): io.flow.common.v0.models.ExpandableUser = io.flow.common.v0.mock.Factories.makeUser()

  }

}