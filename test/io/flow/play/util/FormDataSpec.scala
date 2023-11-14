package io.flow.play.util

import play.api.libs.json._

class FormDataSpec extends LibPlaySpec {
  val fdHelper = FormData

  "formDataToJson" must {

    val data: Map[String, Seq[String]] = Map(
      "email" -> Seq("test@flow.io"),
      "name[first]" -> Seq("mike"),
      "name[last]" -> Seq("roth"),
      "one[two][three][four]" -> Seq("haha"),
      "one[two][three][five]" -> Seq("wow"),
      "arr[][arr2][]" -> Seq("fruit", "vegitables"),
      "tags[]" -> Seq("foo", "bar"),
      "yikes" -> Seq("yes", "no"),
      "port" -> Seq("9999"),
    )

    "returns JsValue" in {
      fdHelper.formDataToJson(data) match {
        case _: JsValue => assert(true)
        case _ => assert(false)
      }
    }

    "creates simple json object" in {
      (fdHelper.formDataToJson(data) \ "email").validate[String] match {
        case JsSuccess(succ, _) => succ must be("test@flow.io")
        case JsError(_) => assert(false)
      }
    }

    "creates complex json object" in {
      (fdHelper.formDataToJson(data) \ "name" \ "first").validate[String] match {
        case JsSuccess(succ, _) => succ must be("mike")
        case JsError(_) => assert(false)
      }

      (fdHelper.formDataToJson(data) \ "name" \ "last").validate[String] match {
        case JsSuccess(succ, _) => succ must be("roth")
        case JsError(_) => assert(false)
      }
    }

    "converts numerical value to number" in {
      (fdHelper.formDataToJson(data) \ "port").validate[JsNumber] match {
        case JsSuccess(succ, _) => succ must be(JsNumber(9999))
        case JsError(_) => assert(false)
      }
    }

    "creates simple array json object" in {
      (fdHelper.formDataToJson(data) \ "tags").validate[Seq[String]] match {
        case JsSuccess(succ, _) => succ must be(Seq("foo", "bar"))
        case JsError(_) => assert(false)
      }
    }

    "creates complex array json object" in {
      (fdHelper.formDataToJson(data) \ "arr").validate[Seq[JsValue]] match {
        case JsSuccess(succ, _) => assert((succ.head \ "arr2").toOption.isDefined)
        case JsError(_) => assert(false)
      }
    }

    "takes first instance of non-array key" in {
      (fdHelper.formDataToJson(data) \ "yikes").validate[String] match {
        case JsSuccess(succ, _) => succ must be("yes")
        case JsError(_) => assert(false)
      }
    }
  }
}
