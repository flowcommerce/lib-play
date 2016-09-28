package io.flow.play.util

import io.flow.error.v0.models.GenericError
import play.api.libs.json.JsError

object Validation {

  def invalidJson(errors: JsError): GenericError = {
    error(errors.toString)
  }

  def invalidJsonDocument(): GenericError = {
    error("Content is not valid JSON")
  }

  def invalidSort(messages: Seq[String]): GenericError = {
    errors(messages)
  }

  def error(message: String): GenericError = {
    errors(Seq(message))
  }

  def errors(messages: Seq[String]): GenericError = {
    GenericError(
      messages = messages
    )
  }

}
