package io.flow.play.util

import io.flow.log.RollbarLogger
import play.api.libs.json._

case class LoggingUtil(rollbar: RollbarLogger) {

  val logger = JsonSafeLogger(
    rollbar = rollbar,
    config = JsonSafeLoggerConfig(
      denylistFields = Set(
        "cvv", "number", "token", "email", "email_address",
        "password", "name", "first_name", "last_name", "streets",
        "address1", "address2", "address3",
        "line1", "line2", "line3",
        "phone", "phone_number",
        "account_owner_name", "account_number", "routing_number", "secret_key", "client_secret", "fingerprint"
      ),
      denylistModels = Set("password_change_form", "cipher_form", "ach_authorization_form", "stripe_authentication_data_form"),
      allowlistModelFields = Map(
        "customer" -> Set("number"),
        "harmonized_item_form" -> Set("number"),
        "hs_code" -> Set("code"),
        "hs6" -> Set("code"),
        "hs10" -> Set("code"),
        "item_form" -> Set("number"),
        "line_item_form" -> Set("number"),
        "packaging" -> Set("number"),
        "partner_order_identifier_form" -> Set("number"),
        "organization_put_form" -> Set("name"),
        "tariff_code" -> Set("code")
      )
    )
  )

}

/**
  * @param denylistFields Any value for a field with this name will be redacted
  * @param denylistModels All fields for these models will be redacted
  * @param allowlistModelFields A Map from `type name` to list of fields to white
  *        list of fields to allow in the output
  */
case class JsonSafeLoggerConfig(
  denylistFields: Set[String],
  denylistModels: Set[String],
  allowlistModelFields: Map[String, Set[String]]
)

/**
  * Configures the white lists and black lists that are used to determine
  * exactly which field values are redacted in the log output
  */
case class JsonSafeLogger(
  config: JsonSafeLoggerConfig,
  rollbar: RollbarLogger
) {
  /**
    * Accepts a JsValue, redacting any fields that may contain sensitive data
    * @param value The JsValue itself
    * @param typ The type represented by the JsValue if resolved from the API Builder specification
    */
  def safeJson(
    value: JsValue,
    typ: Option[String] = None
  ): JsValue = {
    if (typ.exists(config.denylistFields) || typ.exists(isTypeDenylisted)) {
      redact(value)

    } else {
      value match {
        case o: JsObject => JsObject(
          o.value.map {
            case (k, v) if isFieldDenylisted(k, typ) => k -> redact(v)
            case (k, v) => {
              v match {
                case _: JsObject => {
                  // Hack to use the key value as the new type name. TODO: Lookup from spec
                  k -> safeJson(v, Some(k))
                }
                case _ => k -> safeJson(v, typ)
              }
            }
          }
        )

        case ar: JsArray => JsArray(ar.value.map { v => safeJson(v) })

        case _ => value
      }
    }
  }

  private[this] def isTypeDenylisted(typ: String): Boolean = {
    config.denylistModels.map(_.toLowerCase.trim).exists(typ.toLowerCase.trim.contains)
  }

  private[this] def isFieldDenylisted(field: String, typ: Option[String]): Boolean = {
    val allowlist = typ.map(_.toLowerCase.trim) match {
      case None => Set.empty[String]
      case Some(t) => config.allowlistModelFields.getOrElse(t, Set.empty[String])
    }

    config.denylistFields.map(_.toLowerCase.trim).diff(allowlist.map(_.toLowerCase.trim)).contains(field)
  }

  private[this] def redact(value: JsValue): JsValue = {
    value match {
      case JsNull => JsNull
      case v: JsBoolean => v
      case _: JsString => JsString("xxx")
      case _: JsNumber => JsNumber(123)
      case ar: JsArray => JsArray(ar.value.map(redact))
      case o: JsObject => JsObject(o.value.map { case (k, v) => k -> redact(v) })
      case _ => {
        rollbar.
          withKeyValue("type", value.getClass.getName).
          warn("Do not know how to redact values for json type - Returning {}")
        Json.obj()
      }
    }
  }

}
