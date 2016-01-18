package io.flow.play.util

import org.scalatest.{Matchers, FunSpec}
import play.api.libs.json._


class FormDataSpec extends FunSpec with Matchers {
  val fdHelper = FormData

  describe("formDataToJson") {

    val data: Map[String, Seq[String]] = Map(
      "email" -> Seq("test@flow.io"),
      "name[first]" -> Seq("mike"),
      "name[last]" -> Seq("roth"),
      "one[two][three][four]" -> Seq("haha"),
      "one[two][three][five]" -> Seq("wow"),
      "arr[][arr2][]" -> Seq("fruit", "vegitables"),
      "tags[]" -> Seq("foo", "bar"),
      "yikes" -> Seq("yes", "no"))

    it("returns JsValue") {
      fdHelper.formDataToJson(data) match {
        case res: JsValue => assert(true)
        case _ => assert(false)
      }
    }

    it("creates simple json object") {
      (fdHelper.formDataToJson(data) \ "email").validate[String] match {
        case JsSuccess(succ,_) => succ should be("test@flow.io")
        case JsError(_) => assert(false)
      }
    }

    it("creates complex json object") {
      (fdHelper.formDataToJson(data) \ "name" \ "first").validate[String] match {
        case JsSuccess(succ,_) => succ should be("mike")
        case JsError(_) => assert(false)
      }

      (fdHelper.formDataToJson(data) \ "name" \ "last").validate[String] match {
        case JsSuccess(succ,_) => succ should be("roth")
        case JsError(_) => assert(false)
      }
    }

    it("creates simple array json object") {
      (fdHelper.formDataToJson(data) \ "tags").validate[Seq[String]] match {
        case JsSuccess(succ,_) => succ should be(Seq("foo", "bar"))
        case JsError(_) => assert(false)
      }
    }

    it("creates complex array json object") {
      (fdHelper.formDataToJson(data) \ "arr").validate[Seq[JsValue]] match {
        case JsSuccess(succ,_) => assert((succ.head \ "arr2").toOption.isDefined)
        case JsError(_) => assert(false)
      }
    }

    it("takes first instance of non-array key") {
      (fdHelper.formDataToJson(data) \ "yikes").validate[String] match {
        case JsSuccess(succ,_) => succ should be("yes")
        case JsError(_) => assert(false)
      }
    }
  }
}
