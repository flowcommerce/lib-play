package io.flow.play.standalone

import io.flow.log.RollbarLogger
import io.flow.util.FlowEnvironment
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag
import scala.util.control.NonFatal

/** Provides a foundation for an opinionated application that performs work and then terminates, as opposed to a
  * long-running Play service.
  *
  * A default set of modules are loaded by default (see reference.conf) to support common behaviors such as
  * configuration, logging and metrics reporting.
  *
  * Example usage:
  * {{{
  * object MyApp extends StandaloneApp {
  *   override def run(args: Array[String])(implicit app: Application): Unit = {
  *     val myObj = inject[MyObj]
  *     // do something
  *   }
  * }
  * }}}
  */
trait StandaloneApp {
  def run(args: Array[String])(implicit app: Application): Unit

  final def main(args: Array[String]): Unit =
    StandaloneApp.withApplication(environment) { app =>
      run(args)(app)
    }

  final def inject[T: ClassTag](implicit app: Application): T = app.injector.instanceOf[T]

  final def rollbar(implicit app: Application): RollbarLogger = inject[RollbarLogger]

  /** StandaloneApp implementations can be provided in unit tests using GuiceFakeApplicationFactory.
    * {{{
    * object MyApp extends StandaloneApp
    *
    * class MyAppSpec extends PlaySpec with GuiceOneAppPerSuite { override def fakeApplication(): Application = MyApp.build() }
    * }}}
    */
  final def build(): Application = StandaloneApp.build(environment)

  def environment: Environment = StandaloneApp.DefaultEnvironment
}

object StandaloneApp {
  lazy val DefaultEnvironment: Environment = {
    val mode = FlowEnvironment.Current match {
      case FlowEnvironment.Development | FlowEnvironment.Workstation => Mode.Test
      case FlowEnvironment.Production => Mode.Prod
    }
    // See play.core.server.RealServerProcess regarding use of classLoader.  Added this after noticing
    // that a logback.xml in from src/main/resources was not being picked up.
    Environment
      .simple(mode = mode)
      .copy(classLoader = Thread.currentThread().getContextClassLoader)
  }

  def withApplication[T](
    environment: Environment = DefaultEnvironment,
  )(f: Application => T): T =
    try {
      val app = build(environment)
      run(app)(f)
    } catch {
      case NonFatal(t) =>
        t.printStackTrace()
        throw t
    }

  private def build(environment: Environment): Application = {
    val app = new GuiceApplicationBuilder(environment).build()

    // Force a flush here if the application has not flushed logs yet. Rollbar by default flushes every 10 seconds.
    // We could try moving this to the module, however it is currently in lib-log and not Play aware.
    val logger = app.injector.instanceOf[RollbarLogger]
    val applicationLifecycle = app.injector.instanceOf[ApplicationLifecycle]
    applicationLifecycle.addStopHook(() => Future.successful(logger.rollbar.foreach(_.close(true))))

    app
  }

  private def run[T](app: Application)(f: Application => T): T = {
    def stopApp(): Unit = {
      Await.result(app.stop(), 30.seconds)
      ()
    }

    try {
      f(app)
    } finally {
      stopApp()
    }
  }
}
