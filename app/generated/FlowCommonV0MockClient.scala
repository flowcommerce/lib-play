/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.10.20
 * apibuilder 0.15.11 app.apibuilder.io/flow/common/latest/play_2_8_mock_client
 */
package io.flow.common.v0.mock {

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
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

    def makeDiscountTarget(): io.flow.common.v0.models.DiscountTarget = io.flow.common.v0.models.DiscountTarget.Item

    def makeEntityIdentifierType(): io.flow.common.v0.models.EntityIdentifierType = io.flow.common.v0.models.EntityIdentifierType.Ioss

    def makeEnvironment(): io.flow.common.v0.models.Environment = io.flow.common.v0.models.Environment.Sandbox

    def makeExceptionType(): io.flow.common.v0.models.ExceptionType = io.flow.common.v0.models.ExceptionType.Open

    def makeGoodsSupply(): io.flow.common.v0.models.GoodsSupply = io.flow.common.v0.models.GoodsSupply.Export

    def makeHolidayCalendar(): io.flow.common.v0.models.HolidayCalendar = io.flow.common.v0.models.HolidayCalendar.UsBankHolidays

    def makeIncludedLevyKey(): io.flow.common.v0.models.IncludedLevyKey = io.flow.common.v0.models.IncludedLevyKey.Duty

    def makeIncoterm(): io.flow.common.v0.models.Incoterm = io.flow.common.v0.models.Incoterm.Exw

    def makeInputSpecificationType(): io.flow.common.v0.models.InputSpecificationType = io.flow.common.v0.models.InputSpecificationType.Text

    def makeMarginType(): io.flow.common.v0.models.MarginType = io.flow.common.v0.models.MarginType.Fixed

    def makeMeasurementSystem(): io.flow.common.v0.models.MeasurementSystem = io.flow.common.v0.models.MeasurementSystem.Imperial

    def makeMerchantOfRecord(): io.flow.common.v0.models.MerchantOfRecord = io.flow.common.v0.models.MerchantOfRecord.Flow

    def makeOrderMerchantOfRecord(): io.flow.common.v0.models.OrderMerchantOfRecord = io.flow.common.v0.models.OrderMerchantOfRecord.Flow

    def makeOrganizationStatus(): io.flow.common.v0.models.OrganizationStatus = io.flow.common.v0.models.OrganizationStatus.Active

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

    def makeBillingAddress(): io.flow.common.v0.models.BillingAddress = io.flow.common.v0.models.BillingAddress(
      name = None,
      streets = None,
      city = None,
      province = None,
      postal = None,
      country = None,
      company = None
    )

    def makeCatalogItemReference(): io.flow.common.v0.models.CatalogItemReference = io.flow.common.v0.models.CatalogItemReference(
      id = Factories.randomString(24),
      number = Factories.randomString(24)
    )

    def makeCatalogItemSummary(): io.flow.common.v0.models.CatalogItemSummary = io.flow.common.v0.models.CatalogItemSummary(
      number = Factories.randomString(24),
      name = Factories.randomString(24),
      attributes = Map()
    )

    def makeCheckoutReference(): io.flow.common.v0.models.CheckoutReference = io.flow.common.v0.models.CheckoutReference(
      id = Factories.randomString(24)
    )

    def makeContact(): io.flow.common.v0.models.Contact = io.flow.common.v0.models.Contact(
      name = io.flow.common.v0.mock.Factories.makeName(),
      company = None,
      email = None,
      phone = None
    )

    def makeCustomerInvoice(): io.flow.common.v0.models.CustomerInvoice = io.flow.common.v0.models.CustomerInvoice(
      address = None
    )

    def makeCustomerReference(): io.flow.common.v0.models.CustomerReference = io.flow.common.v0.models.CustomerReference(
      number = Factories.randomString(24)
    )

    def makeDatetimeRange(): io.flow.common.v0.models.DatetimeRange = io.flow.common.v0.models.DatetimeRange(
      from = _root_.org.joda.time.DateTime.now,
      to = _root_.org.joda.time.DateTime.now
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

    def makeDiscountForm(): io.flow.common.v0.models.DiscountForm = io.flow.common.v0.models.DiscountForm(
      offer = io.flow.common.v0.mock.Factories.makeDiscountOffer(),
      target = io.flow.common.v0.mock.Factories.makeDiscountTarget(),
      label = None
    )

    def makeDiscountOfferFixed(): io.flow.common.v0.models.DiscountOfferFixed = io.flow.common.v0.models.DiscountOfferFixed(
      money = io.flow.common.v0.mock.Factories.makeMoney()
    )

    def makeDiscountOfferPercent(): io.flow.common.v0.models.DiscountOfferPercent = io.flow.common.v0.models.DiscountOfferPercent(
      percent = BigDecimal("1")
    )

    def makeDiscountsForm(): io.flow.common.v0.models.DiscountsForm = io.flow.common.v0.models.DiscountsForm(
      discounts = Nil
    )

    def makeDuration(): io.flow.common.v0.models.Duration = io.flow.common.v0.models.Duration(
      unit = io.flow.common.v0.mock.Factories.makeUnitOfTime(),
      value = 1L
    )

    def makeEntityIdentifier(): io.flow.common.v0.models.EntityIdentifier = io.flow.common.v0.models.EntityIdentifier(
      name = io.flow.common.v0.mock.Factories.makeEntityIdentifierType(),
      number = Factories.randomString(24)
    )

    def makeException(): io.flow.common.v0.models.Exception = io.flow.common.v0.models.Exception(
      `type` = io.flow.common.v0.mock.Factories.makeExceptionType(),
      datetimeRange = io.flow.common.v0.mock.Factories.makeDatetimeRange()
    )

    def makeExperienceSummary(): io.flow.common.v0.models.ExperienceSummary = io.flow.common.v0.models.ExperienceSummary(
      id = Factories.randomString(24),
      key = Factories.randomString(24),
      name = Factories.randomString(24),
      country = None,
      currency = None,
      language = None
    )

    def makeIncludedLevies(): io.flow.common.v0.models.IncludedLevies = io.flow.common.v0.models.IncludedLevies(
      key = io.flow.common.v0.mock.Factories.makeIncludedLevyKey(),
      label = Factories.randomString(24)
    )

    def makeInputForm(): io.flow.common.v0.models.InputForm = io.flow.common.v0.models.InputForm(
      values = None
    )

    def makeInputFormSpecification(): io.flow.common.v0.models.InputFormSpecification = io.flow.common.v0.models.InputFormSpecification(
      inputs = None,
      limitations = None
    )

    def makeInputSpecification(): io.flow.common.v0.models.InputSpecification = io.flow.common.v0.models.InputSpecification(
      `type` = io.flow.common.v0.mock.Factories.makeInputSpecificationType(),
      name = Factories.randomString(24),
      displayText = None
    )

    def makeInputSpecificationLimitationMax(): io.flow.common.v0.models.InputSpecificationLimitationMax = io.flow.common.v0.models.InputSpecificationLimitationMax(
      max = 1L
    )

    def makeInputSpecificationLimitations(): io.flow.common.v0.models.InputSpecificationLimitations = io.flow.common.v0.models.InputSpecificationLimitations(
      limitations = None
    )

    def makeItemReference(): io.flow.common.v0.models.ItemReference = io.flow.common.v0.models.ItemReference(
      number = Factories.randomString(24)
    )

    def makeLineItem(): io.flow.common.v0.models.LineItem = io.flow.common.v0.models.LineItem(
      number = Factories.randomString(24),
      quantity = 1L,
      price = io.flow.common.v0.mock.Factories.makeMoney(),
      attributes = Map(),
      center = None,
      discount = None
    )

    def makeLineItemAttributesForm(): io.flow.common.v0.models.LineItemAttributesForm = io.flow.common.v0.models.LineItemAttributesForm(
      attributes = Map()
    )

    def makeLineItemForm(): io.flow.common.v0.models.LineItemForm = io.flow.common.v0.models.LineItemForm(
      number = Factories.randomString(24),
      quantity = 1L,
      shipmentEstimate = None,
      price = None,
      attributes = None,
      center = None,
      discount = None,
      discounts = None
    )

    def makeLineItemQuantityForm(): io.flow.common.v0.models.LineItemQuantityForm = io.flow.common.v0.models.LineItemQuantityForm(
      quantity = 1L
    )

    def makeMargin(): io.flow.common.v0.models.Margin = io.flow.common.v0.models.Margin(
      `type` = io.flow.common.v0.mock.Factories.makeMarginType(),
      value = BigDecimal("1")
    )

    def makeMeasurement(): io.flow.common.v0.models.Measurement = io.flow.common.v0.models.Measurement(
      value = Factories.randomString(24),
      units = io.flow.common.v0.mock.Factories.makeUnitOfMeasurement()
    )

    def makeMerchantOfRecordEntity(): io.flow.common.v0.models.MerchantOfRecordEntity = io.flow.common.v0.models.MerchantOfRecordEntity(
      organization = io.flow.common.v0.mock.Factories.makeOrganizationReference(),
      name = Factories.randomString(24),
      vat = None,
      identifiers = None,
      streets = Nil,
      city = Factories.randomString(24),
      province = None,
      postal = None,
      country = Factories.randomString(24),
      phone = None,
      email = None
    )

    def makeMerchantOfRecordEntityRegistration(): io.flow.common.v0.models.MerchantOfRecordEntityRegistration = io.flow.common.v0.models.MerchantOfRecordEntityRegistration(
      number = Factories.randomString(24),
      country = Factories.randomString(24)
    )

    def makeMoney(): io.flow.common.v0.models.Money = io.flow.common.v0.models.Money(
      amount = 1.0,
      currency = Factories.randomString(24)
    )

    def makeMoneyWithBase(): io.flow.common.v0.models.MoneyWithBase = io.flow.common.v0.models.MoneyWithBase(
      currency = Factories.randomString(24),
      amount = 1.0,
      base = io.flow.common.v0.mock.Factories.makeMoney()
    )

    def makeMoneyWithOptionalBase(): io.flow.common.v0.models.MoneyWithOptionalBase = io.flow.common.v0.models.MoneyWithOptionalBase(
      currency = Factories.randomString(24),
      amount = 1.0,
      base = None
    )

    def makeName(): io.flow.common.v0.models.Name = io.flow.common.v0.models.Name(
      first = None,
      last = None
    )

    def makeOrderCustomer(): io.flow.common.v0.models.OrderCustomer = io.flow.common.v0.models.OrderCustomer(
      name = io.flow.common.v0.mock.Factories.makeName(),
      number = None,
      phone = None,
      email = None,
      address = None,
      invoice = None
    )

    def makeOrderCustomerForm(): io.flow.common.v0.models.OrderCustomerForm = io.flow.common.v0.models.OrderCustomerForm(
      name = None,
      number = None,
      phone = None,
      email = None,
      address = None,
      invoice = None
    )

    def makeOrganization(): io.flow.common.v0.models.Organization = io.flow.common.v0.models.Organization(
      id = Factories.randomString(24),
      name = Factories.randomString(24),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      parent = None,
      defaults = None,
      createdAt = None,
      status = io.flow.common.v0.mock.Factories.makeOrganizationStatus()
    )

    def makeOrganizationDefaults(): io.flow.common.v0.models.OrganizationDefaults = io.flow.common.v0.models.OrganizationDefaults(
      country = Factories.randomString(24),
      baseCurrency = Factories.randomString(24),
      language = Factories.randomString(24),
      locale = Factories.randomString(24),
      timezone = Factories.randomString(24)
    )

    def makeOrganizationReference(): io.flow.common.v0.models.OrganizationReference = io.flow.common.v0.models.OrganizationReference(
      id = Factories.randomString(24)
    )

    def makeOrganizationSummary(): io.flow.common.v0.models.OrganizationSummary = io.flow.common.v0.models.OrganizationSummary(
      id = Factories.randomString(24),
      name = Factories.randomString(24),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment()
    )

    def makePartnerReference(): io.flow.common.v0.models.PartnerReference = io.flow.common.v0.models.PartnerReference(
      id = Factories.randomString(24)
    )

    def makePrice(): io.flow.common.v0.models.Price = io.flow.common.v0.models.Price(
      amount = 1.0,
      currency = Factories.randomString(24),
      label = Factories.randomString(24)
    )

    def makePriceForm(): io.flow.common.v0.models.PriceForm = io.flow.common.v0.models.PriceForm(
      amount = 1.0,
      currency = Factories.randomString(24)
    )

    def makePriceSourceCatalog(): io.flow.common.v0.models.PriceSourceCatalog = io.flow.common.v0.models.PriceSourceCatalog(
      price = io.flow.common.v0.mock.Factories.makeMoney()
    )

    def makePriceSourcePriceBook(): io.flow.common.v0.models.PriceSourcePriceBook = io.flow.common.v0.models.PriceSourcePriceBook(
      price = io.flow.common.v0.mock.Factories.makeMoney(),
      includes = io.flow.common.v0.mock.Factories.makeIncludedLevies(),
      priceBookReference = io.flow.common.v0.mock.Factories.makePriceSourcePriceBookReference()
    )

    def makePriceSourcePriceBookReference(): io.flow.common.v0.models.PriceSourcePriceBookReference = io.flow.common.v0.models.PriceSourcePriceBookReference(
      id = Factories.randomString(24),
      key = Factories.randomString(24)
    )

    def makePriceSourceProvided(): io.flow.common.v0.models.PriceSourceProvided = io.flow.common.v0.models.PriceSourceProvided(
      price = io.flow.common.v0.mock.Factories.makeMoney()
    )

    def makePriceWithBase(): io.flow.common.v0.models.PriceWithBase = io.flow.common.v0.models.PriceWithBase(
      currency = Factories.randomString(24),
      amount = 1.0,
      label = Factories.randomString(24),
      base = None
    )

    def makeRepeatDaily(): io.flow.common.v0.models.RepeatDaily = io.flow.common.v0.models.RepeatDaily(
      interval = 1
    )

    def makeRepeatHourly(): io.flow.common.v0.models.RepeatHourly = io.flow.common.v0.models.RepeatHourly(
      interval = 1
    )

    def makeRepeatMonthly(): io.flow.common.v0.models.RepeatMonthly = io.flow.common.v0.models.RepeatMonthly(
      interval = 1,
      days = Nil
    )

    def makeRepeatWeekly(): io.flow.common.v0.models.RepeatWeekly = io.flow.common.v0.models.RepeatWeekly(
      interval = 1,
      daysOfWeek = Nil
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

    def makeSessionReference(): io.flow.common.v0.models.SessionReference = io.flow.common.v0.models.SessionReference(
      id = Factories.randomString(24)
    )

    def makeUser(): io.flow.common.v0.models.User = io.flow.common.v0.models.User(
      id = Factories.randomString(24),
      email = None,
      name = io.flow.common.v0.mock.Factories.makeName(),
      status = io.flow.common.v0.mock.Factories.makeUserStatus()
    )

    def makeUserReference(): io.flow.common.v0.models.UserReference = io.flow.common.v0.models.UserReference(
      id = Factories.randomString(24)
    )

    def makeZone(): io.flow.common.v0.models.Zone = io.flow.common.v0.models.Zone(
      postals = None,
      provinces = None,
      country = Factories.randomString(24)
    )

    def makeDiscountOffer(): io.flow.common.v0.models.DiscountOffer = io.flow.common.v0.mock.Factories.makeDiscountOfferFixed()

    def makeExpandableOrganization(): io.flow.common.v0.models.ExpandableOrganization = io.flow.common.v0.mock.Factories.makeOrganization()

    def makeExpandableUser(): io.flow.common.v0.models.ExpandableUser = io.flow.common.v0.mock.Factories.makeUser()

    def makeInputSpecificationLimitation(): io.flow.common.v0.models.InputSpecificationLimitation = io.flow.common.v0.mock.Factories.makeInputSpecificationLimitationMax()

    def makePriceSource(): io.flow.common.v0.models.PriceSource = io.flow.common.v0.mock.Factories.makePriceSourcePriceBook()

    def makeRepeatSchedule(): io.flow.common.v0.models.RepeatSchedule = io.flow.common.v0.mock.Factories.makeRepeatHourly()

  }

}