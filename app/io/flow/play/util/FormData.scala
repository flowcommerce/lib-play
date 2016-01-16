package io.flow.play.util

import play.api.libs.json.{JsObject, Json, JsValue}


object FormData {
  def convertFormDataValuesToJson(data: Map[String, Seq[String]]): Iterable[JsValue] = {
    data.map{ case (key, value) =>
      key.split("\\[").foldRight(
        if(key.contains("[]"))
          Json.toJson(value)  //take seq for arrays
        else
          Json.toJson(value.head)
      ){ case (newKey, v) =>
        val newVal = {
          val js = (v \ "").getOrElse(v)
          if(newKey == "]"){
            if(!js.toString.startsWith("[")) {
              val s = (v \ "").getOrElse(v).toString.
                replaceFirst("\\{", "[{").
                reverse.
                replaceFirst("\\}", "]}").   //because its reversed
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
  }

  def formDataToJson(data: Map[String, Seq[String]]): JsValue = {
    val nested = convertFormDataValuesToJson(data)

    Json.toJson(nested.foldLeft(Json.obj()){ case (a, b) => a.deepMerge(b.as[JsObject]) })
  }
}
