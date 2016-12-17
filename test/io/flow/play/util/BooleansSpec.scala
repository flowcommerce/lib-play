package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class BooleansSpec extends FunSpec with Matchers {

  it("parse") {
    Booleans.parse("") should be(None)
    Booleans.parse("  ") should be(None)
    Booleans.parse(" Foo ") should be(None)

    Booleans.parse(" t ") should be(Some(true))
    Booleans.parse(" TRUE ") should be(Some(true))

    Booleans.parse(" f ") should be(Some(false))
    Booleans.parse(" FALSE ") should be(Some(false))
  }

  it("parses true values") {
    Booleans.TrueValues.foreach { v =>
      Booleans.parse(v) should be(Some(true))
    }
  }

  it("parses false values") {
    Booleans.FalseValues.foreach { v =>
      Booleans.parse(v) should be(Some(false))
    }
  }

}
