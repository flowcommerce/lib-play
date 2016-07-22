/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.52
 * apidoc:0.11.32 http://www.apidoc.me/flow/common/0.0.52/play_2_4_mock_client
 */
package io.flow.common.v0.mock {

  trait Client extends io.flow.common.v0.interfaces.Client {

    val baseUrl = "http://mock.localhost"

    override def healthchecks: MockHealthchecks = MockHealthchecksImpl

  }

  object MockHealthchecksImpl extends MockHealthchecks

  trait MockHealthchecks extends io.flow.common.v0.Healthchecks {

    def getHealthcheck(
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = scala.concurrent.Future {
      io.flow.common.v0.mock.Factories.makeHealthcheck()
    }

  }

  object Factories {

    def randomString(): String = {
      "Test " + _root_.java.util.UUID.randomUUID.toString.replaceAll("-", " ")
    }

    def makeCalendar() = io.flow.common.v0.models.Calendar.Weekdays

    def makeCapability() = io.flow.common.v0.models.Capability.Crossdock

    def makeChangeType() = io.flow.common.v0.models.ChangeType.Insert

    def makeDimensionType() = io.flow.common.v0.models.DimensionType.Product

    def makeEnvironment() = io.flow.common.v0.models.Environment.Sandbox

    def makeExceptionType() = io.flow.common.v0.models.ExceptionType.Open

    def makeHolidayCalendar() = io.flow.common.v0.models.HolidayCalendar.UsBankHolidays

    def makeMeasurementSystem() = io.flow.common.v0.models.MeasurementSystem.Imperial

    def makeRole() = io.flow.common.v0.models.Role.Admin

    def makeScheduleExceptionStatus() = io.flow.common.v0.models.ScheduleExceptionStatus.Open

    def makeSortDirection() = io.flow.common.v0.models.SortDirection.Ascending

    def makeUnitOfMeasurement() = io.flow.common.v0.models.UnitOfMeasurement.Millimeter

    def makeUnitOfTime() = io.flow.common.v0.models.UnitOfTime.Day

    def makeValueAddedService() = io.flow.common.v0.models.ValueAddedService.HazardousMaterial

    def makeVisibility() = io.flow.common.v0.models.Visibility.Public

    def makeContact() = io.flow.common.v0.models.Contact(
      name = io.flow.common.v0.mock.Factories.makeName(),
      email = None,
      phone = None
    )

    def makeDatetimeRange() = io.flow.common.v0.models.DatetimeRange(
      from = new org.joda.time.DateTime(),
      to = new org.joda.time.DateTime()
    )

    def makeDimension() = io.flow.common.v0.models.Dimension(
      `type` = io.flow.common.v0.mock.Factories.makeDimensionType(),
      depth = None,
      diameter = None,
      length = None,
      weight = None,
      width = None
    )

    def makeError() = io.flow.common.v0.models.Error(
      code = randomString(),
      message = randomString()
    )

    def makeException() = io.flow.common.v0.models.Exception(
      `type` = io.flow.common.v0.mock.Factories.makeExceptionType(),
      datetimeRange = io.flow.common.v0.mock.Factories.makeDatetimeRange()
    )

    def makeExperienceSummary() = io.flow.common.v0.models.ExperienceSummary(
      id = randomString(),
      key = randomString(),
      name = randomString(),
      currency = None,
      country = None
    )

    def makeHealthcheck() = io.flow.common.v0.models.Healthcheck(
      status = randomString()
    )

    def makeLocation() = io.flow.common.v0.models.Location(
      text = None,
      streets = None,
      city = None,
      province = None,
      postal = None,
      country = None,
      latitude = None,
      longitude = None
    )

    def makeLocationReference() = io.flow.common.v0.models.LocationReference(
      text = None
    )

    def makeMeasurement() = io.flow.common.v0.models.Measurement(
      value = randomString(),
      units = io.flow.common.v0.mock.Factories.makeUnitOfMeasurement()
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

    def makeSchedule() = io.flow.common.v0.models.Schedule(
      calendar = None,
      holiday = io.flow.common.v0.mock.Factories.makeHolidayCalendar(),
      exception = Nil,
      cutoff = None
    )

    def makeUser() = io.flow.common.v0.models.User(
      id = randomString(),
      email = None,
      name = io.flow.common.v0.mock.Factories.makeName()
    )

    def makeUserReference() = io.flow.common.v0.models.UserReference(
      id = randomString()
    )

    def makeUserSummary() = io.flow.common.v0.models.UserSummary(
      id = randomString(),
      email = None,
      name = randomString()
    )

    def makeExpandableLocation() = io.flow.common.v0.mock.Factories.makeLocation()

    def makeExpandableOrganization() = io.flow.common.v0.mock.Factories.makeOrganization()

    def makeExpandableUser() = io.flow.common.v0.mock.Factories.makeUser()

  }

}