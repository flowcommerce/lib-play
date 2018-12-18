package io.flow.play.util

import com.github.ghik.silencer.silent

@silent class FlowEnvironmentSpec extends LibPlaySpec {

  "fromString" in {
    FlowEnvironment.fromString("development") must be(Some(FlowEnvironment.Development))
    FlowEnvironment.fromString("production") must be(Some(FlowEnvironment.Production))
    FlowEnvironment.fromString("workstation") must be(Some(FlowEnvironment.Workstation))
  }

  "Current is defined" in {
    FlowEnvironment.all.contains(FlowEnvironment.Current) must be(true)
  }

  "parse" in {
    FlowEnvironment.parse("test", "development") must be(FlowEnvironment.Development)
    FlowEnvironment.parse("test", "production") must be(FlowEnvironment.Production)
    intercept[Throwable] {
      FlowEnvironment.parse("test", "other")
    }.getMessage must be("Value[other] from test[FLOW_ENV] is invalid. Valid values are: development, production, workstation")
  }

}
