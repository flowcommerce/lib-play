package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class EanHelperSpec extends FunSpec with Matchers {

  // example in http://forums.devx.com/showthread.php?172712-how-to-calculate-the-check-digit-(EAN-13)-barcode-symbologies
  it("checksum() returns correct digit") {
    EanHelper.checksum("890400021003") should be (7)
  }

  it("valid() returns true when valid") {
    EanHelper.validate("8904000210037") should be (Nil)
  }

  it("generate() returns a valid EAN13") {
    1.to(100).foreach { i =>
      EanHelper.validate(EanHelper.generate()) should be (Nil)
    }
  }

  it("validate() ") {
    EanHelper.validate("8904000210031") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210032") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210033") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210034") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210035") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210036") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210038") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210039") should be (Seq("Check digit is incorrect"))
    EanHelper.validate("8904000210030") should be (Seq("Check digit is incorrect"))
  }

  it("validate() throw error if not numeric") {
    EanHelper.validate("123456789012n") should be (Seq("Input should be numeric string"))
  }

  it("validate() input should be 13 chars") {
    EanHelper.validate("") should be (Seq(s"Input should be ${EanHelper.fullLength} chars"))
    EanHelper.validate("12345678901234") should be (Seq(s"Input should be ${EanHelper.fullLength} chars"))
  }

  it("checksum() throw error if not numeric") {
    intercept[AssertionError] {
      EanHelper.checksum("123456789n")
    }.getMessage should be("assertion failed: Input [123456789n] should be numeric string")
  }

  it("checksum() input should be 12 chars") {
    intercept[AssertionError] {
      EanHelper.checksum("1234567890123")
    }.getMessage should be("assertion failed: Input [1234567890123] should be 12 chars")
  }

}
