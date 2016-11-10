package io.flow.play.util

import play.api.Logger
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import play.api.http.HttpFilters

/**
  * To use in any Flow app depending on lib-play:
  *
  * (1) Add this to your base.conf:
  *    play.http.filters=io.flow.play.util.LoggingFilter
  *
  **/

class LoggingFilter @javax.inject.Inject() (loggingFilter: FlowLoggingFilter) extends HttpFilters {
  def filters = Seq(loggingFilter)
}

class FlowLoggingFilter @javax.inject.Inject() (
  implicit ec: ExecutionContext,
  config: Config
) extends Filter {
  val LoggedRequestMethodConfig = "play.http.filters.logging.methods"
  val DefaultLoggedRequestMethods = Seq("GET", "PATCH", "POST", "PUT", "DELETE", "OPTIONS")

  def apply(f: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis
    f(requestHeader).map { result =>
      /**
        * If user defined a list of methods that produce logs, then use that
        * Otherwise default to list defined here, which is everything
        */
      if (config.optionalList(LoggedRequestMethodConfig).getOrElse(DefaultLoggedRequestMethods).contains(requestHeader.method)) {
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        val headerMap = requestHeader.headers.toMap
        val line = Seq(
          requestHeader.method,
          s"${requestHeader.host}${requestHeader.uri}",
          result.header.status,
          s"${requestTime}ms",
          headerMap.getOrElse("X-Flow-Request-Id", Nil).mkString(","),
          headerMap.getOrElse("User-Agent", Nil).mkString(","),
          headerMap.getOrElse("X-Forwarded-For", Nil).mkString(","),
          headerMap.getOrElse("CF-Connecting-IP", Nil).mkString(",")
        ).mkString(" ")

        Logger.info(line)
      }

      result
    }
  }
}
