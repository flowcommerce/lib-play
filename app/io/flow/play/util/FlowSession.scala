package io.flow.play.util

import cats.implicits._
import cats.data.Validated
import io.flow.util.Constants


/**
 * Makes available key data from the flow session. These data
 * come from the JWT Headers usually set by the API proxy. We
 * do not make ALL session data available - but provide a base
 * class here to expose more information over time as it becomes
 * critical (as well as providing a strongly typed class to
 * store the session id)
 */
case class FlowSession(id: String) {
  FlowSession.assertValidSessionId(id)
}

object FlowSession {
  private[this] def invalidSessionIdMessage(id: String): String = {
    s"Flow session id must start with '${Constants.Prefixes.Session}' and not[${id.substring(0, 3)}]"
  }

  private[this] def isIdValid(id: String): Boolean = {
    id.startsWith(Constants.Prefixes.Session)
  }

  def assertValidSessionId(id: String): Unit = {
    assert(isIdValid(id), invalidSessionIdMessage(id))
  }

  def validate(id: String): Validated[String, FlowSession] = {
    if (isIdValid(id)) {
      FlowSession(id).valid
    } else {
      invalidSessionIdMessage(id).invalid
    }
  }
}
