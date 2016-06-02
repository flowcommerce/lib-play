/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.49
 * apidoc:0.11.26 http://www.apidoc.me/flow/registry/0.0.49/ning_1_9_client
 */
package io.flow.registry.v0.models {

  case class Application(
    id: String,
    ports: Seq[io.flow.registry.v0.models.Port],
    dependencies: Seq[String]
  )

  case class ApplicationForm(
    id: String,
    service: String,
    external: _root_.scala.Option[Long] = None,
    internal: _root_.scala.Option[Long] = None,
    dependency: _root_.scala.Option[Seq[String]] = None
  )

  case class ApplicationPutForm(
    service: _root_.scala.Option[String] = None,
    external: _root_.scala.Option[Long] = None,
    internal: _root_.scala.Option[Long] = None,
    dependency: _root_.scala.Option[Seq[String]] = None
  )

  case class ApplicationVersion(
    id: String,
    timestamp: _root_.org.joda.time.DateTime,
    `type`: io.flow.common.v0.models.ChangeType,
    application: io.flow.registry.v0.models.Application
  )

  case class Port(
    service: io.flow.registry.v0.models.ServiceReference,
    external: Long,
    internal: Long
  )

  /**
   * A service is used to identify what type of software is actually running. We use
   * this to enable setting up application types with enough configuration info by
   * default to support our use cases around docker, CI, etc. The name service comes
   * from
   * https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.txt
   */
  case class Service(
    id: String,
    defaultPort: Long
  )

  case class ServiceForm(
    id: String,
    defaultPort: Long
  )

  case class ServicePutForm(
    defaultPort: Long
  )

  case class ServiceReference(
    id: String
  )

  case class ServiceVersion(
    id: String,
    timestamp: _root_.org.joda.time.DateTime,
    `type`: io.flow.common.v0.models.ChangeType,
    service: io.flow.registry.v0.models.Service
  )

}

package io.flow.registry.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.common.v0.models.json._
    import io.flow.registry.v0.models.json._

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

    implicit def jsonReadsRegistryApplication: play.api.libs.json.Reads[Application] = {
      (
        (__ \ "id").read[String] and
        (__ \ "ports").read[Seq[io.flow.registry.v0.models.Port]] and
        (__ \ "dependencies").read[Seq[String]]
      )(Application.apply _)
    }

    def jsObjectApplication(obj: io.flow.registry.v0.models.Application) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "ports" -> play.api.libs.json.Json.toJson(obj.ports),
        "dependencies" -> play.api.libs.json.Json.toJson(obj.dependencies)
      )
    }

    implicit def jsonWritesRegistryApplication: play.api.libs.json.Writes[Application] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.Application] {
        def writes(obj: io.flow.registry.v0.models.Application) = {
          jsObjectApplication(obj)
        }
      }
    }

    implicit def jsonReadsRegistryApplicationForm: play.api.libs.json.Reads[ApplicationForm] = {
      (
        (__ \ "id").read[String] and
        (__ \ "service").read[String] and
        (__ \ "external").readNullable[Long] and
        (__ \ "internal").readNullable[Long] and
        (__ \ "dependency").readNullable[Seq[String]]
      )(ApplicationForm.apply _)
    }

    def jsObjectApplicationForm(obj: io.flow.registry.v0.models.ApplicationForm) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "service" -> play.api.libs.json.JsString(obj.service)
      ) ++ (obj.external match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("external" -> play.api.libs.json.JsNumber(x))
      }) ++
      (obj.internal match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("internal" -> play.api.libs.json.JsNumber(x))
      }) ++
      (obj.dependency match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("dependency" -> play.api.libs.json.Json.toJson(x))
      })
    }

    implicit def jsonWritesRegistryApplicationForm: play.api.libs.json.Writes[ApplicationForm] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ApplicationForm] {
        def writes(obj: io.flow.registry.v0.models.ApplicationForm) = {
          jsObjectApplicationForm(obj)
        }
      }
    }

    implicit def jsonReadsRegistryApplicationPutForm: play.api.libs.json.Reads[ApplicationPutForm] = {
      (
        (__ \ "service").readNullable[String] and
        (__ \ "external").readNullable[Long] and
        (__ \ "internal").readNullable[Long] and
        (__ \ "dependency").readNullable[Seq[String]]
      )(ApplicationPutForm.apply _)
    }

    def jsObjectApplicationPutForm(obj: io.flow.registry.v0.models.ApplicationPutForm) = {
      (obj.service match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("service" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.external match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("external" -> play.api.libs.json.JsNumber(x))
      }) ++
      (obj.internal match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("internal" -> play.api.libs.json.JsNumber(x))
      }) ++
      (obj.dependency match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("dependency" -> play.api.libs.json.Json.toJson(x))
      })
    }

    implicit def jsonWritesRegistryApplicationPutForm: play.api.libs.json.Writes[ApplicationPutForm] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ApplicationPutForm] {
        def writes(obj: io.flow.registry.v0.models.ApplicationPutForm) = {
          jsObjectApplicationPutForm(obj)
        }
      }
    }

    implicit def jsonReadsRegistryApplicationVersion: play.api.libs.json.Reads[ApplicationVersion] = {
      (
        (__ \ "id").read[String] and
        (__ \ "timestamp").read[_root_.org.joda.time.DateTime] and
        (__ \ "type").read[io.flow.common.v0.models.ChangeType] and
        (__ \ "application").read[io.flow.registry.v0.models.Application]
      )(ApplicationVersion.apply _)
    }

    def jsObjectApplicationVersion(obj: io.flow.registry.v0.models.ApplicationVersion) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "timestamp" -> play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(obj.timestamp)),
        "type" -> play.api.libs.json.JsString(obj.`type`.toString),
        "application" -> jsObjectApplication(obj.application)
      )
    }

    implicit def jsonWritesRegistryApplicationVersion: play.api.libs.json.Writes[ApplicationVersion] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ApplicationVersion] {
        def writes(obj: io.flow.registry.v0.models.ApplicationVersion) = {
          jsObjectApplicationVersion(obj)
        }
      }
    }

    implicit def jsonReadsRegistryPort: play.api.libs.json.Reads[Port] = {
      (
        (__ \ "service").read[io.flow.registry.v0.models.ServiceReference] and
        (__ \ "external").read[Long] and
        (__ \ "internal").read[Long]
      )(Port.apply _)
    }

    def jsObjectPort(obj: io.flow.registry.v0.models.Port) = {
      play.api.libs.json.Json.obj(
        "service" -> jsObjectServiceReference(obj.service),
        "external" -> play.api.libs.json.JsNumber(obj.external),
        "internal" -> play.api.libs.json.JsNumber(obj.internal)
      )
    }

    implicit def jsonWritesRegistryPort: play.api.libs.json.Writes[Port] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.Port] {
        def writes(obj: io.flow.registry.v0.models.Port) = {
          jsObjectPort(obj)
        }
      }
    }

    implicit def jsonReadsRegistryService: play.api.libs.json.Reads[Service] = {
      (
        (__ \ "id").read[String] and
        (__ \ "default_port").read[Long]
      )(Service.apply _)
    }

    def jsObjectService(obj: io.flow.registry.v0.models.Service) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "default_port" -> play.api.libs.json.JsNumber(obj.defaultPort)
      )
    }

    implicit def jsonWritesRegistryService: play.api.libs.json.Writes[Service] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.Service] {
        def writes(obj: io.flow.registry.v0.models.Service) = {
          jsObjectService(obj)
        }
      }
    }

    implicit def jsonReadsRegistryServiceForm: play.api.libs.json.Reads[ServiceForm] = {
      (
        (__ \ "id").read[String] and
        (__ \ "default_port").read[Long]
      )(ServiceForm.apply _)
    }

    def jsObjectServiceForm(obj: io.flow.registry.v0.models.ServiceForm) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "default_port" -> play.api.libs.json.JsNumber(obj.defaultPort)
      )
    }

    implicit def jsonWritesRegistryServiceForm: play.api.libs.json.Writes[ServiceForm] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ServiceForm] {
        def writes(obj: io.flow.registry.v0.models.ServiceForm) = {
          jsObjectServiceForm(obj)
        }
      }
    }

    implicit def jsonReadsRegistryServicePutForm: play.api.libs.json.Reads[ServicePutForm] = {
      (__ \ "default_port").read[Long].map { x => new ServicePutForm(defaultPort = x) }
    }

    def jsObjectServicePutForm(obj: io.flow.registry.v0.models.ServicePutForm) = {
      play.api.libs.json.Json.obj(
        "default_port" -> play.api.libs.json.JsNumber(obj.defaultPort)
      )
    }

    implicit def jsonWritesRegistryServicePutForm: play.api.libs.json.Writes[ServicePutForm] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ServicePutForm] {
        def writes(obj: io.flow.registry.v0.models.ServicePutForm) = {
          jsObjectServicePutForm(obj)
        }
      }
    }

    implicit def jsonReadsRegistryServiceReference: play.api.libs.json.Reads[ServiceReference] = {
      (__ \ "id").read[String].map { x => new ServiceReference(id = x) }
    }

    def jsObjectServiceReference(obj: io.flow.registry.v0.models.ServiceReference) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id)
      )
    }

    implicit def jsonWritesRegistryServiceReference: play.api.libs.json.Writes[ServiceReference] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ServiceReference] {
        def writes(obj: io.flow.registry.v0.models.ServiceReference) = {
          jsObjectServiceReference(obj)
        }
      }
    }

    implicit def jsonReadsRegistryServiceVersion: play.api.libs.json.Reads[ServiceVersion] = {
      (
        (__ \ "id").read[String] and
        (__ \ "timestamp").read[_root_.org.joda.time.DateTime] and
        (__ \ "type").read[io.flow.common.v0.models.ChangeType] and
        (__ \ "service").read[io.flow.registry.v0.models.Service]
      )(ServiceVersion.apply _)
    }

    def jsObjectServiceVersion(obj: io.flow.registry.v0.models.ServiceVersion) = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "timestamp" -> play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(obj.timestamp)),
        "type" -> play.api.libs.json.JsString(obj.`type`.toString),
        "service" -> jsObjectService(obj.service)
      )
    }

    implicit def jsonWritesRegistryServiceVersion: play.api.libs.json.Writes[ServiceVersion] = {
      new play.api.libs.json.Writes[io.flow.registry.v0.models.ServiceVersion] {
        def writes(obj: io.flow.registry.v0.models.ServiceVersion) = {
          jsObjectServiceVersion(obj)
        }
      }
    }
  }
}



package io.flow.registry.v0 {
  import com.ning.http.client.{AsyncCompletionHandler, AsyncHttpClient, AsyncHttpClientConfig, Realm, Request, RequestBuilder, Response}

  object Constants {

    val BaseUrl = "https://registry.api.flow.io"
    val Namespace = "io.flow.registry.v0"
    val UserAgent = "apidoc:0.11.26 http://www.apidoc.me/flow/registry/0.0.49/ning_1_9_client"
    val Version = "0.0.49"
    val VersionMajor = 0

  }

  class Client(
    val baseUrl: String = "https://registry.api.flow.io",
    auth: scala.Option[io.flow.registry.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil,
    asyncHttpClient: AsyncHttpClient = Client.defaultAsyncHttpClient
  ) extends interfaces.Client {
    import org.slf4j.{Logger, LoggerFactory}
    import io.flow.common.v0.models.json._
    import io.flow.registry.v0.models.json._

    def closeAsyncHttpClient() {
      asyncHttpClient.close()
    }

    val logger = LoggerFactory.getLogger(getClass)

    def applications: Applications = Applications

    def healthchecks: Healthchecks = Healthchecks

    def services: Services = Services

    object Applications extends Applications {
      override def get(
        id: _root_.scala.Option[Seq[String]] = None,
        port: _root_.scala.Option[Seq[Long]] = None,
        service: _root_.scala.Option[Seq[String]] = None,
        prefix: _root_.scala.Option[String] = None,
        q: _root_.scala.Option[String] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "-created_at",
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.Application]] = {
        val queryParameters = Seq(
          prefix.map("prefix" -> _),
          q.map("q" -> _),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _) ++
          port.getOrElse(Nil).map("port" -> _.toString) ++
          service.getOrElse(Nil).map("service" -> _)

        _executeRequest("GET", s"/applications", queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("Seq[io.flow.registry.v0.models.Application]", r, _.validate[Seq[io.flow.registry.v0.models.Application]])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def getVersions(
        id: _root_.scala.Option[Seq[String]] = None,
        application: _root_.scala.Option[Seq[String]] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "journal_timestamp",
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.ApplicationVersion]] = {
        val queryParameters = Seq(
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _) ++
          application.getOrElse(Nil).map("application" -> _)

        _executeRequest("GET", s"/applications/versions", queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("Seq[io.flow.registry.v0.models.ApplicationVersion]", r, _.validate[Seq[io.flow.registry.v0.models.ApplicationVersion]])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def getById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application] = {
        _executeRequest("GET", s"/applications/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Application", r, _.validate[io.flow.registry.v0.models.Application])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 404 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401, 404", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def post(
        applicationForm: io.flow.registry.v0.models.ApplicationForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application] = {
        val payload = play.api.libs.json.Json.toJson(applicationForm)

        _executeRequest("POST", s"/applications", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 201 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Application", r, _.validate[io.flow.registry.v0.models.Application])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 422 => throw new io.flow.registry.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 201, 401, 422", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def putById(
        id: String,
        applicationPutForm: io.flow.registry.v0.models.ApplicationPutForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application] = {
        val payload = play.api.libs.json.Json.toJson(applicationPutForm)

        _executeRequest("PUT", s"/applications/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Application", r, _.validate[io.flow.registry.v0.models.Application])
          case r if r.getStatusCode == 201 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Application", r, _.validate[io.flow.registry.v0.models.Application])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 422 => throw new io.flow.registry.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 201, 401, 422", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def deleteById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/applications/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 204 => ()
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 404 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 204, 401, 404", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }
    }

    object Healthchecks extends Healthchecks {
      override def getHealthcheck(
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = {
        _executeRequest("GET", s"/_internal_/healthcheck", requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.common.v0.models.Healthcheck", r, _.validate[io.flow.common.v0.models.Healthcheck])
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }
    }

    object Services extends Services {
      override def get(
        id: _root_.scala.Option[Seq[String]] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "-created_at",
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.Service]] = {
        val queryParameters = Seq(
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _)

        _executeRequest("GET", s"/services", queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("Seq[io.flow.registry.v0.models.Service]", r, _.validate[Seq[io.flow.registry.v0.models.Service]])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def getVersions(
        id: _root_.scala.Option[Seq[String]] = None,
        service: _root_.scala.Option[Seq[String]] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "journal_timestamp",
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.ServiceVersion]] = {
        val queryParameters = Seq(
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _) ++
          service.getOrElse(Nil).map("service" -> _)

        _executeRequest("GET", s"/services/versions", queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("Seq[io.flow.registry.v0.models.ServiceVersion]", r, _.validate[Seq[io.flow.registry.v0.models.ServiceVersion]])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def getById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service] = {
        _executeRequest("GET", s"/services/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Service", r, _.validate[io.flow.registry.v0.models.Service])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 404 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 401, 404", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def post(
        serviceForm: io.flow.registry.v0.models.ServiceForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service] = {
        val payload = play.api.libs.json.Json.toJson(serviceForm)

        _executeRequest("POST", s"/services", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 201 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Service", r, _.validate[io.flow.registry.v0.models.Service])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 422 => throw new io.flow.registry.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 201, 401, 422", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def putById(
        id: String,
        servicePutForm: io.flow.registry.v0.models.ServicePutForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service] = {
        val payload = play.api.libs.json.Json.toJson(servicePutForm)

        _executeRequest("PUT", s"/services/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 200 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Service", r, _.validate[io.flow.registry.v0.models.Service])
          case r if r.getStatusCode == 201 => _root_.io.flow.registry.v0.Client.parseJson("io.flow.registry.v0.models.Service", r, _.validate[io.flow.registry.v0.models.Service])
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 422 => throw new io.flow.registry.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 200, 201, 401, 422", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }

      override def deleteById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/services/${_root_.io.flow.registry.v0.PathSegment.encode(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.getStatusCode == 204 => ()
          case r if r.getStatusCode == 401 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 404 => throw new io.flow.registry.v0.errors.UnitResponse(r.getStatusCode)
          case r if r.getStatusCode == 422 => throw new io.flow.registry.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Unsupported response code[${r.getStatusCode}]. Expected: 204, 401, 404, 422", requestUri = Some(r.getUri.toJavaNetURI))
        }
      }
    }

    def _logRequest(request: Request) {
      logger.info("_logRequest: " + request)
    }

    def _requestBuilder(method: String, path: String, requestHeaders: Seq[(String, String)]): RequestBuilder = {
      val builder = new RequestBuilder(method)
        .setUrl(baseUrl + path)
        .addHeader("User-Agent", Constants.UserAgent)
        .addHeader("X-Apidoc-Version", Constants.Version)
        .addHeader("X-Apidoc-Version-Major", Constants.VersionMajor.toString)

      defaultHeaders.foreach { h => builder.addHeader(h._1, h._2) }
      requestHeaders.foreach { h => builder.addHeader(h._1, h._2) }

      auth.fold(builder) {
        case Authorization.Basic(username, passwordOpt) => {
          builder.setRealm(
            new Realm.RealmBuilder()
              .setPrincipal(username)
              .setPassword(passwordOpt.getOrElse(""))
              .setUsePreemptiveAuth(true)
              .setScheme(Realm.AuthScheme.BASIC)
              .build()
          )
        }
        case a => sys.error("Invalid authorization scheme[" + a.getClass + "]")
      }
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Nil,
      requestHeaders: Seq[(String, String)] = Nil,
      body: Option[play.api.libs.json.JsValue] = None
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.ning.http.client.Response] = {
      val allHeaders = body match {
        case None => requestHeaders
        case Some(_) => _withJsonContentType(requestHeaders)
      }

      val request = _requestBuilder(method, path, allHeaders)

      queryParameters.foreach { pair =>
        request.addQueryParam(pair._1, pair._2)
      }

      val requestWithParamsAndBody = body.fold(request) { b =>
        val serialized = play.api.libs.json.Json.stringify(b)
        request.setBody(serialized)
      }

      val finalRequest = requestWithParamsAndBody.build()
      _logRequest(finalRequest)

      val result = scala.concurrent.Promise[com.ning.http.client.Response]()
      asyncHttpClient.executeRequest(finalRequest,
        new AsyncCompletionHandler[Unit]() {
          override def onCompleted(r: com.ning.http.client.Response) = result.success(r)
          override def onThrowable(t: Throwable) = result.failure(t)
        }
      )
      result.future
    }

    /**
     * Adds a Content-Type: application/json header unless the specified requestHeaders
     * already contain a Content-Type header
     */
    def _withJsonContentType(headers: Seq[(String, String)]): Seq[(String, String)] = {
      headers.find { _._1.toUpperCase == "CONTENT-TYPE" } match {
        case None => headers ++ Seq(("Content-Type" -> "application/json; charset=UTF-8"))
        case Some(_) => headers
      }
    }

  }

  object Client {

    private lazy val defaultAsyncHttpClient = {
      new AsyncHttpClient(
        new AsyncHttpClientConfig.Builder()
          .setExecutorService(java.util.concurrent.Executors.newCachedThreadPool())
          .build()
      )
    }

    def parseJson[T](
      className: String,
      r: _root_.com.ning.http.client.Response,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.getResponseBody("UTF-8"))) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw new io.flow.registry.v0.errors.FailedRequest(r.getStatusCode, s"Invalid json for class[" + className + "]: " + errors.mkString(" "), requestUri = Some(r.getUri.toJavaNetURI))
        }
      }
    }

  }

  sealed trait Authorization
  object Authorization {
    case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def baseUrl: String
      def applications: io.flow.registry.v0.Applications
      def healthchecks: io.flow.registry.v0.Healthchecks
      def services: io.flow.registry.v0.Services
    }

  }

  trait Applications {
    /**
     * Search applications. Always paginated.
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      port: _root_.scala.Option[Seq[Long]] = None,
      service: _root_.scala.Option[Seq[String]] = None,
      prefix: _root_.scala.Option[String] = None,
      q: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.Application]]

    /**
     * Provides visibility into recent changes of each application, including deletion
     */
    def getVersions(
      id: _root_.scala.Option[Seq[String]] = None,
      application: _root_.scala.Option[Seq[String]] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "journal_timestamp",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.ApplicationVersion]]

    /**
     * Returns information about a specific application.
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application]

    /**
     * Create a new application.
     */
    def post(
      applicationForm: io.flow.registry.v0.models.ApplicationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application]

    /**
     * Upsert an application with the specified id.
     */
    def putById(
      id: String,
      applicationPutForm: io.flow.registry.v0.models.ApplicationPutForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Application]

    /**
     * Delete the application with this id
     */
    def deleteById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  trait Healthchecks {
    def getHealthcheck(
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck]
  }

  trait Services {
    /**
     * Search services. Always paginated.
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.Service]]

    /**
     * Provides visibility into recent changes of each service, including deletion
     */
    def getVersions(
      id: _root_.scala.Option[Seq[String]] = None,
      service: _root_.scala.Option[Seq[String]] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "journal_timestamp",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.registry.v0.models.ServiceVersion]]

    /**
     * Returns information about a specific service.
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service]

    /**
     * Create a new service.
     */
    def post(
      serviceForm: io.flow.registry.v0.models.ServiceForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service]

    /**
     * Upsert an service with the specified id.
     */
    def putById(
      id: String,
      servicePutForm: io.flow.registry.v0.models.ServicePutForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.registry.v0.models.Service]

    /**
     * Delete the service with this id
     */
    def deleteById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import io.flow.common.v0.models.json._
    import io.flow.registry.v0.models.json._

    case class ErrorsResponse(
      response: _root_.com.ning.http.client.Response,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.getStatusCode + ": " + response.getResponseBody("UTF-8"))){
      lazy val errors = _root_.io.flow.registry.v0.Client.parseJson("Seq[io.flow.common.v0.models.Error]", response, _.validate[Seq[io.flow.common.v0.models.Error]])
    }

    case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

  object PathSegment {
    // See https://github.com/playframework/playframework/blob/2.3.x/framework/src/play/src/main/scala/play/utils/UriEncoding.scala
    def encode(s: String, inputCharset: String): String = {
      val in = s.getBytes(inputCharset)
      val out = new java.io.ByteArrayOutputStream()
      for (b <- in) {
        val allowed = segmentChars.get(b & 0xFF)
        if (allowed) {
          out.write(b)
        } else {
          out.write('%')
          out.write(upperHex((b >> 4) & 0xF))
          out.write(upperHex(b & 0xF))
        }
      }
      out.toString("US-ASCII")
    }

    private def upperHex(x: Int): Int = {
      // Assume 0 <= x < 16
      if (x < 10) (x + '0') else (x - 10 + 'A')
    }

    private[this] val segmentChars: java.util.BitSet = membershipTable(pchar)

    private def pchar: Seq[Char] = {
      val alphaDigit = for ((min, max) <- Seq(('a', 'z'), ('A', 'Z'), ('0', '9')); c <- min to max) yield c
      val unreserved = alphaDigit ++ Seq('-', '.', '_', '~')
      val subDelims = Seq('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=')
      unreserved ++ subDelims ++ Seq(':', '@')
    }

    private def membershipTable(chars: Seq[Char]): java.util.BitSet = {
      val bits = new java.util.BitSet(256)
      for (c <- chars) { bits.set(c.toInt) }
      bits
    }
  }
}