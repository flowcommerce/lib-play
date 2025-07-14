package io.flow.play.util

import play.api.libs.json._

object FormData {
  def formDataToJson(data: Map[String, Seq[String]]): JsValue = {
    val nested = data.map { case (key, value) =>
      key
        .split("\\[")
        .foldRight(
          if (key.contains("[]"))
            Json.toJson(value) // take seq for arrays
          else
            Json.toJson(value.headOption.getOrElse("")),
        ) { case (newKey, v) =>
          val newVal = {
            val js = (v \ "").getOrElse(v)

            // convert '{key: val}' to '[{key: val}]' if previous key specifies array type, otherwise nothing
            if (newKey == "]") {
              if (!js.toString.startsWith("[")) {
                val s =
                  (v \ "").getOrElse(v).toString.replaceFirst("\\{", "[{").reverse.replaceFirst("\\}", "]}").reverse

                Json.toJson(Json.parse(s))
              } else
                js
            } else {
              safeConvertToLong(js.toString.replace("\"", "")) match {
                case Some(num) => JsNumber(num)
                case _ => js
              }
            }
          }

          Json.obj(newKey.replace("]", "") -> newVal)
        }
    }

    Json.toJson(nested.foldLeft(Json.obj()) { case (a, b) => a.deepMerge(b.as[JsObject]) })
  }

  def safeConvertToLong(s: String): Option[Long] = {
    try {
      Some(s.toLong)
    } catch {
      case _: Exception => None
    }
  }
}
