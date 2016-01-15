package io.flow.play.util

import play.api.libs.json.{JsObject, Json, JsValue}


object FormDataHelper {
  def convertFormDataValuesToJson(data: Map[String, Seq[String]]): Map[String, JsValue] = {
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

  def formDataToJson(data: Map[String, Seq[String]]): JsValue = {
    val nested = convertFormDataValuesToJson(data).map(fd =>
      if(fd._1.contains(".")) {
        fd._1.split("\\.").foldRight(fd._2) { case (key, value) =>
          Json.obj(key -> value)}
      }
      else
        Json.obj(fd._1 -> fd._2)
    )

    Json.toJson(nested.foldLeft(Json.obj()){ case (a, b) => a.deepMerge(b.as[JsObject]) })
  }
}
