package io.flow.play.util

import io.flow.play.clients.MockConfig

import com.typesafe.config.ConfigFactory
import play.api.Configuration

class ConfigSpec extends LibPlaySpec {

  private[this] lazy val config = MockConfig(DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty()))))

  "optionalList" in {
    config.optionalList(createTestId()) must be(None)

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
    }.getMessage must be("FlowError Configuration variable[foo] is required")

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
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[other]: must be an int")

  }

  "optionalPositiveInt" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalPositiveInt("foo") must be(Some(5))

    config.set("foo", "0")
    intercept[RuntimeException] {
      config.optionalPositiveInt("foo")
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[0]: must be > 0")

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalPositiveInt("foo")
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[other]: must be an int")
  }

  "requiredPositiveInt" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredPositiveInt(name)
    }.getMessage must be(s"FlowError Configuration variable[$name] is required")
  }

  "requiredInt" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredInt(name)
    }.getMessage must be(s"FlowError Configuration variable[$name] is required")
  }

  "optionalLong" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalLong("foo") must be(Some(5))

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalLong("foo")
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[other]: must be a long")

  }

  "requiredLong" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredLong(name)
    }.getMessage must be(s"FlowError Configuration variable[$name] is required")
  }

  "optionalPositiveLong" in {
    config.optionalString(createTestId()) must be(None)

    config.set("foo", "5")
    config.optionalPositiveLong("foo") must be(Some(5))

    config.set("foo", "0")
    intercept[RuntimeException] {
      config.optionalPositiveLong("foo")
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[0]: must be > 0")

    config.set("foo", "other")
    intercept[RuntimeException] {
      config.optionalPositiveLong("foo")
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[other]: must be a long")
  }

  "requiredPositiveLong" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredPositiveLong(name)
    }.getMessage must be(s"FlowError Configuration variable[$name] is required")
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
    }.getMessage must be("FlowError Configuration variable[foo] has invalid value[other]. Use true, t, false, or f")

  }

  "requiredBoolean" in {
    val name = createTestId()
    intercept[RuntimeException] {
      config.requiredBoolean(name)
    }.getMessage must be(s"FlowError Configuration variable[$name] is required")
  }

}
