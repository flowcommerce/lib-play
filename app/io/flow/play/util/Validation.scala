package io.flow.play.util

import io.flow.error.v0.models.ValidationError
import play.api.libs.json.JsError

object Validation {

  def invalidJson(errors: JsError): ValidationError = {
    error(errors.toString)
  }

  def invalidJsonDocument(): ValidationError = {
    error("Content is not valid JSON")
  }

  def invalidSort(messages: Seq[String]): ValidationError = {
    errors(messages)
  }

  def error(message: String): ValidationError = {
    errors(Seq(message))
  }

  def errors(messages: Seq[String]): ValidationError = {
    ValidationError(
      messages = messages
    )
  }

}
