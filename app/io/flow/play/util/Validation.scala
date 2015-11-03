package io.flow.play.util

import io.flow.common.v0.models.Error
import play.api.libs.json.{Json, JsError}

trait Validated {

  def errors: Seq[Error]

  def assertValid() {
    assert(errors.isEmpty, errors.map(_.message).mkString(" and "))
  }

}

object Validation {

  private[this] val InvalidJsonCode = "invalid_json"
  private[this] val UserAuthorizationFailedCode = "user_authorization_failed"
  private[this] val ErrorCode = "validation_error"
  private[this] val ServerError = "server_error"

  def invalidJson(errors: JsError): Seq[Error] = {
    Seq(Error(InvalidJsonCode, errors.toString))
  }

  def invalidJsonDocument(): Seq[Error] = {
    Seq(Error(InvalidJsonCode, "Content is not valid JSON"))
  }

  def userAuthorizationFailed(): Seq[Error] = {
    Seq(Error(UserAuthorizationFailedCode, "Email address and/or password did not match"))
  }

  def error(message: String): Seq[Error] = {
    errors(Seq(message))
  }

  def errors(messages: Seq[String]): Seq[Error] = {
    messages.map { msg => Error(ErrorCode, msg) }
  }

  def serverError(error: String = "Internal Server Error"): Seq[Error] = {
    Seq(Error(ServerError, error))
  }

}
