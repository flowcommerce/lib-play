package io.flow.play.util

import play.api.libs.json.{JsObject, Json, JsValue}


object FormData {
  def convertFormDataValuesDotNotation(data: Map[String, Seq[String]]): Map[String, JsValue] = {
    data.map { case (key, value) =>
      key -> (value match {
        case a: Seq[Any] => {
          a.toList match {
            case one :: Nil => {
              // Normally we just have single parameters -
              // we default to NON array json value.
              Json.toJson(one)
            }
            case _ => {
              Json.toJson(a)
            }
          }
        }
        case _ => {
          Json.toJson(value)
        }
      })
    }
  }

  def convertFormDataValuesBracketNoations(data: Map[String, Seq[String]]): Iterable[JsValue] = {
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
    val nested =
      if (data.keySet.exists(k => k.contains(".")))
        convertFormDataValuesDotNotation(data).map(fd =>
          fd._1.split("\\.").foldRight(fd._2) { case (key, value) => Json.obj(key -> value) })
      else
        convertFormDataValuesBracketNoations(data)

    Json.toJson(nested.foldLeft(Json.obj()){ case (a, b) => a.deepMerge(b.as[JsObject]) })
  }
}
