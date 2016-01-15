package io.flow.play.util

import org.scalatest.{Matchers, FunSpec}
import play.api.libs.json.JsValue


class FormDataSpec extends FunSpec with Matchers {
  val fdHelper = FormData

  describe("convertFormDataValuesToJson") {

    it("creates JsValue values") {
      val data: Map[String, Seq[String]] = Map(
        "field1" -> Seq("value"),
        "field2.custom1" -> Seq("value"),
        "field2.custom2" -> Seq("value"))

      fdHelper.convertFormDataValuesToJson(data) match {
        case res: Map[String, JsValue] => assert(true)
        case _ => assert(false)
      }
    }
  }

  describe("formDataToJson") {

    it("creates nested json") {
      val data: Map[String, Seq[String]] = Map(
        "field1" -> Seq("value"),
        "field2.custom1" -> Seq("value"),
        "field2.custom2" -> Seq("value"))

      fdHelper.formDataToJson(data).toString should be("{\"field1\":\"value\",\"field2\":{\"custom1\":\"value\",\"custom2\":\"value\"}}")
    }
  }
}
