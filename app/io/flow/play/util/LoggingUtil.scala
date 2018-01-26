package io.flow.play.util

import net.logstash.logback.marker.Markers.appendEntries
import play.api.{Logger, MarkerContext}

import scala.collection.JavaConverters._

trait Logging {
  protected lazy val logger = Logger(getClass)

  @inline def logWith(fields: (String, Any)*): MarkerContext = {
    MarkerContext(appendEntries(fields.toMap.asJava))
  }
}
