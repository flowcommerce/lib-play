package io.flow.play.util

class BooleansSpec extends LibPlaySpec {

  "parse" in {
    Booleans.parse("") must be(None)
    Booleans.parse("  ") must be(None)
    Booleans.parse(" Foo ") must be(None)

    Booleans.parse(" t ") must be(Some(true))
    Booleans.parse(" TRUE ") must be(Some(true))

    Booleans.parse(" f ") must be(Some(false))
    Booleans.parse(" FALSE ") must be(Some(false))
  }

  "parses true values" in {
    Booleans.TrueValues.foreach { v =>
      Booleans.parse(v) must be(Some(true))
    }
  }

  "parses false values" in {
    Booleans.FalseValues.foreach { v =>
      Booleans.parse(v) must be(Some(false))
    }
  }

}
