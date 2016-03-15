package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class FlowEnvironmentSpec extends FunSpec with Matchers {

  it("fromString") {
    FlowEnvironment.fromString("development") should be(Some(FlowEnvironment.Development))
    FlowEnvironment.fromString("production") should be(Some(FlowEnvironment.Production))
  }

  it("Current is defined") {
    FlowEnvironment.all.contains(FlowEnvironment.Current) should be(true)
  }

  it("parse") {
    FlowEnvironment.parse("test", "development") should be(FlowEnvironment.Development)
    FlowEnvironment.parse("test", "production") should be(FlowEnvironment.Production)
    intercept[Throwable] {
      FlowEnvironment.parse("test", "other")
    }.getMessage should be("Value[other] from test[FLOW_ENV] is invalid. Valid values are: development, production")
  }

}
