package io.flow.play.util

import io.flow.play.clients.MockConfig
import java.util.UUID
import org.scalatest._
import org.scalatestplus.play._

class ConfigSpec extends PlaySpec with OneAppPerSuite {

  private[this] lazy val config = play.api.Play.current.injector.instanceOf[MockConfig]

  def createTestId(): String = UUID.randomUUID.toString

  "optionalList" in {
    config.optionalList(createTestId())must be(None)

    config.set("foo", Seq("a", "b", "c"))
    config.optionalList("foo") must be(Some(Seq("a", "b", "c")))
  }

  "optionalString" in {
    config.optionalString(createTestId())must be(None)

    config.set("foo", "")
    config.optionalString("foo") must be(None)

    config.set("foo", "   ")
    config.optionalString("foo") must be(None)

    config.set("foo", "value")
    config.optionalString("foo") must be(Some("value"))

    config.set("foo", " value ")
    config.optionalString("foo") must be(Some("value"))
  }

  "requiredString" in {
    config.set("foo", "   ")

    intercept[RuntimeException] {
      config.requiredString("foo")
    }.getMessage must be("Configuration variable[foo] is required")

    config.set("foo", "value")
    config.requiredString("foo") must be("value")
  }

  "optionalInt" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalInt("foo") must be(Some(5))

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalInt("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[other]: must be an int")
    
  }

  "optionalPositiveInt" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalPositiveInt("foo") must be(Some(5))

    config.set("foo", "0")
    intercept[RuntimeException] {
      config.optionalPositiveInt("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[0]: must be > 0")
    
    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalPositiveInt("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[other]: must be an int")
  }

  "requiredPositiveInt" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredPositiveInt(name)
    }.getMessage must be(s"Configuration variable[$name] is required")
  }

  "requiredInt" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredInt(name)
    }.getMessage must be(s"Configuration variable[$name] is required")
  }

  "optionalLong" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalLong("foo") must be(Some(5))

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalLong("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[other]: must be a long")
    
  }

  "requiredLong" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredLong(name)
    }.getMessage must be(s"Configuration variable[$name] is required")
  }

  "optionalPositiveLong" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalPositiveLong("foo") must be(Some(5))

    config.set("foo", "0")
    intercept[RuntimeException] {
      config.optionalPositiveLong("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[0]: must be > 0")
    
    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalPositiveLong("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[other]: must be a long")
  }

  "requiredPositiveLong" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredPositiveLong(name)
    }.getMessage must be(s"Configuration variable[$name] is required")
  }

  "optionalBoolean" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "true")
    config.optionalBoolean("foo") must be(Some(true))

    config.set("foo", "t")
    config.optionalBoolean("foo") must be(Some(true))

    config.set("foo", " FALSE ")
    config.optionalBoolean("foo") must be(Some(false))

    config.set("foo", " F ")
    config.optionalBoolean("foo") must be(Some(false))

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalBoolean("foo")
    }.getMessage must be("Configuration variable[foo] has invalid value[other]: must be true, t, false, or f")
    
  }

  "requiredBoolean" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredBoolean(name)
    }.getMessage must be(s"Configuration variable[$name] is required")
  }
  
}
