package io.flow.play.util

import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers.appendEntries
import play.api.Logger

import scala.collection.JavaConverters._

trait Logging {
  protected lazy val logger = Logger(getClass).logger
}

object LoggingUtil {
  def append(fields: (String, Any)*): LogstashMarker = {
    appendEntries(fields.toMap.asJava)
  }
}
