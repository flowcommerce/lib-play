package io.flow.play.util

import play.api.libs.json.{JsObject, Json, JsValue}


object FormData {
  def formDataToJson(data: Map[String, Seq[String]]): JsValue = {
    val nested = data.map{ case (key, value) =>
      key.split("\\[").foldRight(
        if(key.contains("[]"))
          Json.toJson(value)  //take seq for arrays
        else
          Json.toJson(value.headOption.getOrElse(""))
      ){ case (newKey, v) =>
        val newVal = {
          val js = (v \ "").getOrElse(v)

          //convert '{key: val}' to '[{key: val}]' if previous key specifies array type, otherwise nothing
          if(newKey == "]"){
            if(!js.toString.startsWith("[")) {
              val s = (v \ "").getOrElse(v).toString.
                replaceFirst("\\{", "[{").
                reverse.
                replaceFirst("\\}", "]}").
                reverse

              Json.toJson(Json.parse(s))
            }
            else
              js
          }
          else
            js
        }

        Json.obj(newKey.replace("]", "") -> newVal)
      }
    }

    Json.toJson(nested.foldLeft(Json.obj()){ case (a, b) => a.deepMerge(b.as[JsObject]) })
  }
}
