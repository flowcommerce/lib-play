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

  object Codes {
    val InvalidJson = "invalid_json"
    val UserAuthorizationFailed = "user_authorization_failed"
    val Error = "validation_error"
    val ServerError = "server_error"
  }

  def invalidJson(errors: JsError): Seq[Error] = {
    Seq(Error(Codes.InvalidJson, errors.toString))
  }

  def invalidJsonDocument(): Seq[Error] = {
    Seq(Error(Codes.InvalidJson, "Content is not valid JSON"))
  }

  def userAuthorizationFailed(): Seq[Error] = {
    Seq(Error(Codes.UserAuthorizationFailed, "Email address and/or password did not match"))
  }

  def error(message: String): Seq[Error] = {
    errors(Seq(message))
  }

  def errors(messages: Seq[String]): Seq[Error] = {
    messages.map { msg => Error(Codes.Error, msg) }
  }

  def serverError(error: String = "Internal Server Error"): Seq[Error] = {
    Seq(Error(Codes.ServerError, error))
  }

}

/**
  * Utility class to aid in persisting validation information. General
  * concept is to enable UI to validate a form - returning an instance
  * of Validated. This makes it easy then for the data objects to call
  * assertValid() to ensure form data was validated.
  * 
  * Example usage:
  *   val form = UserForm(...)
  *   val validatedForm = validate(form)
  *   UsersDao.create(validatedForm)
  * 
  *   def create(valid: ValidatedForm[UserForm]) {
  *     valid.assertValid()
  *   }
  */
case class ValidatedForm[T](form: T, errorMessages: Seq[String]) extends Validated {

  override def errors = Validation.errors(errorMessages)

}
