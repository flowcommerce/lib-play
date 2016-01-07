/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.5-dev
 * apidoc:0.10.0 http://localhost:9000/flow/common/0.0.5-dev/play_2_x_json
 */
package io.flow.common.v0.models {

  sealed trait ExpandableOrganization

  sealed trait ExpandableUser

  /**
   * Defines structured fields for address to be used in user/form input. Either text
   * or the structured input needs to be present.
   */
  case class Address(
    text: _root_.scala.Option[String] = None,
    streets: _root_.scala.Option[Seq[String]] = None,
    city: _root_.scala.Option[String] = None,
    province: _root_.scala.Option[String] = None,
    postalCode: _root_.scala.Option[String] = None,
    country: _root_.scala.Option[io.flow.common.v0.models.Country] = None
  )

  case class ChangeHeader(
    id: String,
    timestamp: _root_.org.joda.time.DateTime,
    `type`: io.flow.common.v0.models.ChangeType
  )

  case class DatetimeRange(
    from: _root_.org.joda.time.DateTime,
    to: _root_.org.joda.time.DateTime
  )

  case class Dimension(
    value: Double,
    units: io.flow.common.v0.models.UnitOfMeasurement
  )

  case class Error(
    code: String,
    message: String
  )

  case class Healthcheck(
    status: String
  )

  /**
   * We capture the location as a string; over time we anticipate storing structued
   * data by parsing the location (e.g. the country) to enable things like reporting,
   * filtering in bulk
   */
  case class Location(
    value: String
  )

  case class Name(
    first: _root_.scala.Option[String] = None,
    last: _root_.scala.Option[String] = None
  )

  /**
   * Represents a single organization in the system
   */
  case class Organization(
    id: String,
    name: String
  ) extends ExpandableOrganization

  case class OrganizationSummary(
    id: String,
    name: String
  )

  case class Price(
    amount: BigDecimal,
    currency: String
  )

  case class Reference(
    id: String
  ) extends ExpandableOrganization with ExpandableUser

  /**
   * Represents a single user in the system
   */
  case class User(
    id: String,
    email: _root_.scala.Option[String] = None,
    name: io.flow.common.v0.models.Name
  ) extends ExpandableUser

  case class UserSummary(
    id: String,
    name: String
  )

  /**
   * Provides future compatibility in clients - in the future, when a type is added
   * to the union ExpandableOrganization, it will need to be handled in the client
   * code. This implementation will deserialize these future types as an instance of
   * this class.
   */
  case class ExpandableOrganizationUndefinedType(
    description: String
  ) extends ExpandableOrganization

  /**
   * Provides future compatibility in clients - in the future, when a type is added
   * to the union ExpandableUser, it will need to be handled in the client code. This
   * implementation will deserialize these future types as an instance of this class.
   */
  case class ExpandableUserUndefinedType(
    description: String
  ) extends ExpandableUser

  sealed trait Calendar

  object Calendar {

    /**
     * Mon - Fri
     */
    case object Weekdays extends Calendar { override def toString = "Weekdays" }
    /**
     * 7 days per week
     */
    case object Everyday extends Calendar { override def toString = "Everyday" }
    /**
     * We do not yet know the calendar
     */
    case object Unknown extends Calendar { override def toString = "Unknown" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends Calendar

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Weekdays, Everyday, Unknown)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): Calendar = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[Calendar] = byName.get(value.toLowerCase)

  }

  /**
   * TODO: Returns, inventory
   */
  sealed trait Capability

  object Capability {

    case object Crossdock extends Capability { override def toString = "crossdock" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends Capability

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Crossdock)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): Capability = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[Capability] = byName.get(value.toLowerCase)

  }

  sealed trait ChangeType

  object ChangeType {

    case object Insert extends ChangeType { override def toString = "insert" }
    case object Update extends ChangeType { override def toString = "update" }
    case object Delete extends ChangeType { override def toString = "delete" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends ChangeType

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Insert, Update, Delete)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): ChangeType = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[ChangeType] = byName.get(value.toLowerCase)

  }

  /**
   * ISO 4217 3-character country code. See http://www.xe.com/iso4217.php
   */
  sealed trait Country

  object Country {

    case object Usa extends Country { override def toString = "usa" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends Country

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Usa)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): Country = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[Country] = byName.get(value.toLowerCase)

  }

  /**
   * Pending better name
   */
  sealed trait ScheduleExceptionStatus

  object ScheduleExceptionStatus {

    case object Open extends ScheduleExceptionStatus { override def toString = "Open" }
    case object Closed extends ScheduleExceptionStatus { override def toString = "Closed" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends ScheduleExceptionStatus

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Open, Closed)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): ScheduleExceptionStatus = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[ScheduleExceptionStatus] = byName.get(value.toLowerCase)

  }

  /**
   * Defines the units of measurement that we support. As units are added, we conform
   * with the standard units provided by jscience as part of JSR 363 - see
   * http://jscience.org/api/javax/measure/unit/SI.html and
   * http://jscience.org/api/javax/measure/unit/NonSI.html
   */
  sealed trait UnitOfMeasurement

  object UnitOfMeasurement {

    /**
     * Equivalent to MILLI(METRE).
     */
    case object Millimeter extends UnitOfMeasurement { override def toString = "millimeter" }
    /**
     * Equivalent to CENTI(METRE).
     */
    case object Centimeter extends UnitOfMeasurement { override def toString = "centimeter" }
    /**
     * A unit of length equal to 0.01004 m (standard name in).
     */
    case object Inch extends UnitOfMeasurement { override def toString = "inch" }
    /**
     * A unit of length equal to 0.3048 m (standard name ft).
     */
    case object Foot extends UnitOfMeasurement { override def toString = "foot" }
    /**
     * A unit of volume equal to one cubic inch (in³).
     */
    case object CubicInch extends UnitOfMeasurement { override def toString = "cubic_inch" }
    /**
     * The metric unit for volume quantities (m³).
     */
    case object CubicMeter extends UnitOfMeasurement { override def toString = "cubic_meter" }
    /**
     * A unit of mass equal to 1 / 1000 kilogram (standard name g).
     */
    case object Gram extends UnitOfMeasurement { override def toString = "gram" }
    /**
     * The base unit for mass quantities (kg).
     */
    case object Kilogram extends UnitOfMeasurement { override def toString = "kilogram" }
    /**
     * The base unit for length quantities (m).
     */
    case object Meter extends UnitOfMeasurement { override def toString = "meter" }
    /**
     * A unit of mass equal to 1 / 16 POUND (standard name oz).
     */
    case object Ounce extends UnitOfMeasurement { override def toString = "ounce" }
    /**
     * A unit of mass equal to 453.59237 grams (avoirdupois pound, standard name lb).
     */
    case object Pound extends UnitOfMeasurement { override def toString = "pound" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends UnitOfMeasurement

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Millimeter, Centimeter, Inch, Foot, CubicInch, CubicMeter, Gram, Kilogram, Meter, Ounce, Pound)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): UnitOfMeasurement = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[UnitOfMeasurement] = byName.get(value.toLowerCase)

  }

  /**
   * Defines the units of time that we support. We confirm with the standard set of
   * units of time from
   * http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html
   */
  sealed trait UnitOfTime

  object UnitOfTime {

    case object Day extends UnitOfTime { override def toString = "day" }
    case object Hour extends UnitOfTime { override def toString = "hour" }
    case object Minute extends UnitOfTime { override def toString = "minute" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends UnitOfTime

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Day, Hour, Minute)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): UnitOfTime = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[UnitOfTime] = byName.get(value.toLowerCase)

  }

  sealed trait ValueAddedService

  object ValueAddedService {

    /**
     * See https://en.wikipedia.org/wiki/ORM-D
     */
    case object HazardousMaterial extends ValueAddedService { override def toString = "Hazardous Material" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends ValueAddedService

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(HazardousMaterial)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): ValueAddedService = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[ValueAddedService] = byName.get(value.toLowerCase)

  }

  sealed trait Visibility

  object Visibility {

    case object Public extends Visibility { override def toString = "public" }
    case object Private extends Visibility { override def toString = "private" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends Visibility

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Public, Private)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): Visibility = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[Visibility] = byName.get(value.toLowerCase)

  }

}

package io.flow.common.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.common.v0.models.json._

    private[v0] implicit val jsonReadsUUID = __.read[String].map(java.util.UUID.fromString)

    private[v0] implicit val jsonWritesUUID = new Writes[java.util.UUID] {
      def writes(x: java.util.UUID) = JsString(x.toString)
    }

    private[v0] implicit val jsonReadsJodaDateTime = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateTimeParser
      dateTimeParser.parseDateTime(str)
    }

    private[v0] implicit val jsonWritesJodaDateTime = new Writes[org.joda.time.DateTime] {
      def writes(x: org.joda.time.DateTime) = {
        import org.joda.time.format.ISODateTimeFormat.dateTime
        val str = dateTime.print(x)
        JsString(str)
      }
    }

    implicit val jsonReadsCommonCalendar = __.read[String].map(Calendar.apply)
    implicit val jsonWritesCommonCalendar = new Writes[Calendar] {
      def writes(x: Calendar) = JsString(x.toString)
    }

    implicit val jsonReadsCommonCapability = __.read[String].map(Capability.apply)
    implicit val jsonWritesCommonCapability = new Writes[Capability] {
      def writes(x: Capability) = JsString(x.toString)
    }

    implicit val jsonReadsCommonChangeType = __.read[String].map(ChangeType.apply)
    implicit val jsonWritesCommonChangeType = new Writes[ChangeType] {
      def writes(x: ChangeType) = JsString(x.toString)
    }

    implicit val jsonReadsCommonCountry = __.read[String].map(Country.apply)
    implicit val jsonWritesCommonCountry = new Writes[Country] {
      def writes(x: Country) = JsString(x.toString)
    }

    implicit val jsonReadsCommonScheduleExceptionStatus = __.read[String].map(ScheduleExceptionStatus.apply)
    implicit val jsonWritesCommonScheduleExceptionStatus = new Writes[ScheduleExceptionStatus] {
      def writes(x: ScheduleExceptionStatus) = JsString(x.toString)
    }

    implicit val jsonReadsCommonUnitOfMeasurement = __.read[String].map(UnitOfMeasurement.apply)
    implicit val jsonWritesCommonUnitOfMeasurement = new Writes[UnitOfMeasurement] {
      def writes(x: UnitOfMeasurement) = JsString(x.toString)
    }

    implicit val jsonReadsCommonUnitOfTime = __.read[String].map(UnitOfTime.apply)
    implicit val jsonWritesCommonUnitOfTime = new Writes[UnitOfTime] {
      def writes(x: UnitOfTime) = JsString(x.toString)
    }

    implicit val jsonReadsCommonValueAddedService = __.read[String].map(ValueAddedService.apply)
    implicit val jsonWritesCommonValueAddedService = new Writes[ValueAddedService] {
      def writes(x: ValueAddedService) = JsString(x.toString)
    }

    implicit val jsonReadsCommonVisibility = __.read[String].map(Visibility.apply)
    implicit val jsonWritesCommonVisibility = new Writes[Visibility] {
      def writes(x: Visibility) = JsString(x.toString)
    }

    implicit def jsonReadsCommonAddress: play.api.libs.json.Reads[Address] = {
      (
        (__ \ "text").readNullable[String] and
        (__ \ "streets").readNullable[Seq[String]] and
        (__ \ "city").readNullable[String] and
        (__ \ "province").readNullable[String] and
        (__ \ "postal_code").readNullable[String] and
        (__ \ "country").readNullable[io.flow.common.v0.models.Country]
      )(Address.apply _)
    }

    implicit def jsonWritesCommonAddress: play.api.libs.json.Writes[Address] = {
      (
        (__ \ "text").writeNullable[String] and
        (__ \ "streets").writeNullable[Seq[String]] and
        (__ \ "city").writeNullable[String] and
        (__ \ "province").writeNullable[String] and
        (__ \ "postal_code").writeNullable[String] and
        (__ \ "country").writeNullable[io.flow.common.v0.models.Country]
      )(unlift(Address.unapply _))
    }

    implicit def jsonReadsCommonChangeHeader: play.api.libs.json.Reads[ChangeHeader] = {
      (
        (__ \ "id").read[String] and
        (__ \ "timestamp").read[_root_.org.joda.time.DateTime] and
        (__ \ "type").read[io.flow.common.v0.models.ChangeType]
      )(ChangeHeader.apply _)
    }

    implicit def jsonWritesCommonChangeHeader: play.api.libs.json.Writes[ChangeHeader] = {
      (
        (__ \ "id").write[String] and
        (__ \ "timestamp").write[_root_.org.joda.time.DateTime] and
        (__ \ "type").write[io.flow.common.v0.models.ChangeType]
      )(unlift(ChangeHeader.unapply _))
    }

    implicit def jsonReadsCommonDatetimeRange: play.api.libs.json.Reads[DatetimeRange] = {
      (
        (__ \ "from").read[_root_.org.joda.time.DateTime] and
        (__ \ "to").read[_root_.org.joda.time.DateTime]
      )(DatetimeRange.apply _)
    }

    implicit def jsonWritesCommonDatetimeRange: play.api.libs.json.Writes[DatetimeRange] = {
      (
        (__ \ "from").write[_root_.org.joda.time.DateTime] and
        (__ \ "to").write[_root_.org.joda.time.DateTime]
      )(unlift(DatetimeRange.unapply _))
    }

    implicit def jsonReadsCommonDimension: play.api.libs.json.Reads[Dimension] = {
      (
        (__ \ "value").read[Double] and
        (__ \ "units").read[io.flow.common.v0.models.UnitOfMeasurement]
      )(Dimension.apply _)
    }

    implicit def jsonWritesCommonDimension: play.api.libs.json.Writes[Dimension] = {
      (
        (__ \ "value").write[Double] and
        (__ \ "units").write[io.flow.common.v0.models.UnitOfMeasurement]
      )(unlift(Dimension.unapply _))
    }

    implicit def jsonReadsCommonError: play.api.libs.json.Reads[Error] = {
      (
        (__ \ "code").read[String] and
        (__ \ "message").read[String]
      )(Error.apply _)
    }

    implicit def jsonWritesCommonError: play.api.libs.json.Writes[Error] = {
      (
        (__ \ "code").write[String] and
        (__ \ "message").write[String]
      )(unlift(Error.unapply _))
    }

    implicit def jsonReadsCommonHealthcheck: play.api.libs.json.Reads[Healthcheck] = {
      (__ \ "status").read[String].map { x => new Healthcheck(status = x) }
    }

    implicit def jsonWritesCommonHealthcheck: play.api.libs.json.Writes[Healthcheck] = new play.api.libs.json.Writes[Healthcheck] {
      def writes(x: Healthcheck) = play.api.libs.json.Json.obj(
        "status" -> play.api.libs.json.Json.toJson(x.status)
      )
    }

    implicit def jsonReadsCommonLocation: play.api.libs.json.Reads[Location] = {
      (__ \ "value").read[String].map { x => new Location(value = x) }
    }

    implicit def jsonWritesCommonLocation: play.api.libs.json.Writes[Location] = new play.api.libs.json.Writes[Location] {
      def writes(x: Location) = play.api.libs.json.Json.obj(
        "value" -> play.api.libs.json.Json.toJson(x.value)
      )
    }

    implicit def jsonReadsCommonName: play.api.libs.json.Reads[Name] = {
      (
        (__ \ "first").readNullable[String] and
        (__ \ "last").readNullable[String]
      )(Name.apply _)
    }

    implicit def jsonWritesCommonName: play.api.libs.json.Writes[Name] = {
      (
        (__ \ "first").writeNullable[String] and
        (__ \ "last").writeNullable[String]
      )(unlift(Name.unapply _))
    }

    implicit def jsonReadsCommonOrganization: play.api.libs.json.Reads[Organization] = {
      (
        (__ \ "id").read[String] and
        (__ \ "name").read[String]
      )(Organization.apply _)
    }

    implicit def jsonWritesCommonOrganization: play.api.libs.json.Writes[Organization] = {
      (
        (__ \ "id").write[String] and
        (__ \ "name").write[String]
      )(unlift(Organization.unapply _))
    }

    implicit def jsonReadsCommonOrganizationSummary: play.api.libs.json.Reads[OrganizationSummary] = {
      (
        (__ \ "id").read[String] and
        (__ \ "name").read[String]
      )(OrganizationSummary.apply _)
    }

    implicit def jsonWritesCommonOrganizationSummary: play.api.libs.json.Writes[OrganizationSummary] = {
      (
        (__ \ "id").write[String] and
        (__ \ "name").write[String]
      )(unlift(OrganizationSummary.unapply _))
    }

    implicit def jsonReadsCommonPrice: play.api.libs.json.Reads[Price] = {
      (
        (__ \ "amount").read[BigDecimal] and
        (__ \ "currency").read[String]
      )(Price.apply _)
    }

    implicit def jsonWritesCommonPrice: play.api.libs.json.Writes[Price] = {
      (
        (__ \ "amount").write[BigDecimal] and
        (__ \ "currency").write[String]
      )(unlift(Price.unapply _))
    }

    implicit def jsonReadsCommonReference: play.api.libs.json.Reads[Reference] = {
      (__ \ "id").read[String].map { x => new Reference(id = x) }
    }

    implicit def jsonWritesCommonReference: play.api.libs.json.Writes[Reference] = new play.api.libs.json.Writes[Reference] {
      def writes(x: Reference) = play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.Json.toJson(x.id)
      )
    }

    implicit def jsonReadsCommonUser: play.api.libs.json.Reads[User] = {
      (
        (__ \ "id").read[String] and
        (__ \ "email").readNullable[String] and
        (__ \ "name").read[io.flow.common.v0.models.Name]
      )(User.apply _)
    }

    implicit def jsonWritesCommonUser: play.api.libs.json.Writes[User] = {
      (
        (__ \ "id").write[String] and
        (__ \ "email").writeNullable[String] and
        (__ \ "name").write[io.flow.common.v0.models.Name]
      )(unlift(User.unapply _))
    }

    implicit def jsonReadsCommonUserSummary: play.api.libs.json.Reads[UserSummary] = {
      (
        (__ \ "id").read[String] and
        (__ \ "name").read[String]
      )(UserSummary.apply _)
    }

    implicit def jsonWritesCommonUserSummary: play.api.libs.json.Writes[UserSummary] = {
      (
        (__ \ "id").write[String] and
        (__ \ "name").write[String]
      )(unlift(UserSummary.unapply _))
    }

    implicit def jsonReadsCommonExpandableOrganization: play.api.libs.json.Reads[ExpandableOrganization] = new play.api.libs.json.Reads[ExpandableOrganization] {
      def reads(js: play.api.libs.json.JsValue): play.api.libs.json.JsResult[ExpandableOrganization] = {
        (js \ "discriminator").validate[String] match {
          case play.api.libs.json.JsError(msg) => play.api.libs.json.JsError(msg)
          case play.api.libs.json.JsSuccess(discriminator, _) => {
            discriminator match {
              case "reference" => js.validate[io.flow.common.v0.models.Reference]
              case "organization" => js.validate[io.flow.common.v0.models.Organization]
              case other => play.api.libs.json.JsSuccess(io.flow.common.v0.models.ExpandableOrganizationUndefinedType(other))
            }
          }
        }
      }
    }

    implicit def jsonWritesCommonExpandableOrganization: play.api.libs.json.Writes[ExpandableOrganization] = new play.api.libs.json.Writes[ExpandableOrganization] {
      def writes(obj: io.flow.common.v0.models.ExpandableOrganization) = {
        obj match {
          case x: io.flow.common.v0.models.Reference => play.api.libs.json.Json.obj(
            "discriminator" -> "reference",
            "id" -> play.api.libs.json.Json.toJson(x.id)
          )
          case x: io.flow.common.v0.models.Organization => play.api.libs.json.Json.obj(
            "discriminator" -> "organization",
            "id" -> play.api.libs.json.Json.toJson(x.id),
            "name" -> play.api.libs.json.Json.toJson(x.name)
          )
          case x: io.flow.common.v0.models.ExpandableOrganizationUndefinedType => {
            sys.error("The type[io.flow.common.v0.models.ExpandableOrganizationUndefinedType] should never be serialized")
          }
        }
      }
    }

    implicit def jsonReadsCommonExpandableUser: play.api.libs.json.Reads[ExpandableUser] = new play.api.libs.json.Reads[ExpandableUser] {
      def reads(js: play.api.libs.json.JsValue): play.api.libs.json.JsResult[ExpandableUser] = {
        (js \ "discriminator").validate[String] match {
          case play.api.libs.json.JsError(msg) => play.api.libs.json.JsError(msg)
          case play.api.libs.json.JsSuccess(discriminator, _) => {
            discriminator match {
              case "reference" => js.validate[io.flow.common.v0.models.Reference]
              case "user" => js.validate[io.flow.common.v0.models.User]
              case other => play.api.libs.json.JsSuccess(io.flow.common.v0.models.ExpandableUserUndefinedType(other))
            }
          }
        }
      }
    }

    implicit def jsonWritesCommonExpandableUser: play.api.libs.json.Writes[ExpandableUser] = new play.api.libs.json.Writes[ExpandableUser] {
      def writes(obj: io.flow.common.v0.models.ExpandableUser) = {
        obj match {
          case x: io.flow.common.v0.models.Reference => play.api.libs.json.Json.obj(
            "discriminator" -> "reference",
            "id" -> play.api.libs.json.Json.toJson(x.id)
          )
          case x: io.flow.common.v0.models.User => play.api.libs.json.Json.obj(
            "discriminator" -> "user",
            "id" -> play.api.libs.json.Json.toJson(x.id),
            "email" -> play.api.libs.json.Json.toJson(x.email),
            "name" -> play.api.libs.json.Json.toJson(x.name)
          )
          case x: io.flow.common.v0.models.ExpandableUserUndefinedType => {
            sys.error("The type[io.flow.common.v0.models.ExpandableUserUndefinedType] should never be serialized")
          }
        }
      }
    }
  }
}

package io.flow.common.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}
    import org.joda.time.{DateTime, LocalDate}
    import org.joda.time.format.ISODateTimeFormat
    import io.flow.common.v0.models._

    // Type: date-time-iso8601
    implicit val pathBindableTypeDateTimeIso8601 = new PathBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    implicit val queryStringBindableTypeDateTimeIso8601 = new QueryStringBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    // Type: date-iso8601
    implicit val pathBindableTypeDateIso8601 = new PathBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )

    implicit val queryStringBindableTypeDateIso8601 = new QueryStringBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )

    // Enum: Calendar
    private[this] val enumCalendarNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.Calendar.all.mkString(", ")}"

    implicit val pathBindableEnumCalendar = new PathBindable.Parsing[io.flow.common.v0.models.Calendar] (
      Calendar.fromString(_).get, _.toString, enumCalendarNotFound
    )

    implicit val queryStringBindableEnumCalendar = new QueryStringBindable.Parsing[io.flow.common.v0.models.Calendar](
      Calendar.fromString(_).get, _.toString, enumCalendarNotFound
    )

    // Enum: Capability
    private[this] val enumCapabilityNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.Capability.all.mkString(", ")}"

    implicit val pathBindableEnumCapability = new PathBindable.Parsing[io.flow.common.v0.models.Capability] (
      Capability.fromString(_).get, _.toString, enumCapabilityNotFound
    )

    implicit val queryStringBindableEnumCapability = new QueryStringBindable.Parsing[io.flow.common.v0.models.Capability](
      Capability.fromString(_).get, _.toString, enumCapabilityNotFound
    )

    // Enum: ChangeType
    private[this] val enumChangeTypeNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.ChangeType.all.mkString(", ")}"

    implicit val pathBindableEnumChangeType = new PathBindable.Parsing[io.flow.common.v0.models.ChangeType] (
      ChangeType.fromString(_).get, _.toString, enumChangeTypeNotFound
    )

    implicit val queryStringBindableEnumChangeType = new QueryStringBindable.Parsing[io.flow.common.v0.models.ChangeType](
      ChangeType.fromString(_).get, _.toString, enumChangeTypeNotFound
    )

    // Enum: Country
    private[this] val enumCountryNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.Country.all.mkString(", ")}"

    implicit val pathBindableEnumCountry = new PathBindable.Parsing[io.flow.common.v0.models.Country] (
      Country.fromString(_).get, _.toString, enumCountryNotFound
    )

    implicit val queryStringBindableEnumCountry = new QueryStringBindable.Parsing[io.flow.common.v0.models.Country](
      Country.fromString(_).get, _.toString, enumCountryNotFound
    )

    // Enum: ScheduleExceptionStatus
    private[this] val enumScheduleExceptionStatusNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.ScheduleExceptionStatus.all.mkString(", ")}"

    implicit val pathBindableEnumScheduleExceptionStatus = new PathBindable.Parsing[io.flow.common.v0.models.ScheduleExceptionStatus] (
      ScheduleExceptionStatus.fromString(_).get, _.toString, enumScheduleExceptionStatusNotFound
    )

    implicit val queryStringBindableEnumScheduleExceptionStatus = new QueryStringBindable.Parsing[io.flow.common.v0.models.ScheduleExceptionStatus](
      ScheduleExceptionStatus.fromString(_).get, _.toString, enumScheduleExceptionStatusNotFound
    )

    // Enum: UnitOfMeasurement
    private[this] val enumUnitOfMeasurementNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.UnitOfMeasurement.all.mkString(", ")}"

    implicit val pathBindableEnumUnitOfMeasurement = new PathBindable.Parsing[io.flow.common.v0.models.UnitOfMeasurement] (
      UnitOfMeasurement.fromString(_).get, _.toString, enumUnitOfMeasurementNotFound
    )

    implicit val queryStringBindableEnumUnitOfMeasurement = new QueryStringBindable.Parsing[io.flow.common.v0.models.UnitOfMeasurement](
      UnitOfMeasurement.fromString(_).get, _.toString, enumUnitOfMeasurementNotFound
    )

    // Enum: UnitOfTime
    private[this] val enumUnitOfTimeNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.UnitOfTime.all.mkString(", ")}"

    implicit val pathBindableEnumUnitOfTime = new PathBindable.Parsing[io.flow.common.v0.models.UnitOfTime] (
      UnitOfTime.fromString(_).get, _.toString, enumUnitOfTimeNotFound
    )

    implicit val queryStringBindableEnumUnitOfTime = new QueryStringBindable.Parsing[io.flow.common.v0.models.UnitOfTime](
      UnitOfTime.fromString(_).get, _.toString, enumUnitOfTimeNotFound
    )

    // Enum: ValueAddedService
    private[this] val enumValueAddedServiceNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.ValueAddedService.all.mkString(", ")}"

    implicit val pathBindableEnumValueAddedService = new PathBindable.Parsing[io.flow.common.v0.models.ValueAddedService] (
      ValueAddedService.fromString(_).get, _.toString, enumValueAddedServiceNotFound
    )

    implicit val queryStringBindableEnumValueAddedService = new QueryStringBindable.Parsing[io.flow.common.v0.models.ValueAddedService](
      ValueAddedService.fromString(_).get, _.toString, enumValueAddedServiceNotFound
    )

    // Enum: Visibility
    private[this] val enumVisibilityNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.common.v0.models.Visibility.all.mkString(", ")}"

    implicit val pathBindableEnumVisibility = new PathBindable.Parsing[io.flow.common.v0.models.Visibility] (
      Visibility.fromString(_).get, _.toString, enumVisibilityNotFound
    )

    implicit val queryStringBindableEnumVisibility = new QueryStringBindable.Parsing[io.flow.common.v0.models.Visibility](
      Visibility.fromString(_).get, _.toString, enumVisibilityNotFound
    )

  }

}
