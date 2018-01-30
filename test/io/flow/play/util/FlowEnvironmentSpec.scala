package io.flow.play.util

class FlowEnvironmentSpec extends LibPlaySpec {

  "fromString" in {
    FlowEnvironment.fromString("development") must be(Some(FlowEnvironment.Development))
    FlowEnvironment.fromString("production") must be(Some(FlowEnvironment.Production))
  }

  "current is defined" in {
    val fetcher = app.injector.instanceOf[FlowEnvironmentFetcher]
    FlowEnvironment.all.contains(fetcher.current) must be(true)
  }

  "parse" in {
    val fetcher = app.injector.instanceOf[FlowEnvironmentFetcher]
    fetcher.parse("test", "development") must be(FlowEnvironment.Development)
    fetcher.parse("test", "production") must be(FlowEnvironment.Production)
    intercept[Throwable] {
      fetcher.parse("test", "other")
    }.getMessage must be("Value[other] from test[FLOW_ENV] is invalid. Valid values are: development, production, workstation")
  }

}
