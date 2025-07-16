package io.flow.play.standalone

import io.flow.util.FlowEnvironment
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment, Mode}

import scala.concurrent.Await
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.reflect.ClassTag
import scala.util.control.NonFatal

/** Provides a foundation for an opinionated application that performs work and then terminates, as opposed to a
  * long-running Play service.
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
    withApplication { app =>
      run(args)(app)
    }

  final def withApplication[T](f: Application => T): T =
    StandaloneApp.run(build(), shutdownDuration)(f)

  final def inject[T: ClassTag](implicit app: Application): T = app.injector.instanceOf[T]

  def environment: Environment = StandaloneApp.DefaultEnvironment

  def shutdownDuration: FiniteDuration = 10.seconds // Duration to wait for the application to stop normally

  /** StandaloneApp implementations can be provided in unit tests using GuiceFakeApplicationFactory.
    * {{{
    * object MyApp extends StandaloneApp
    *
    * class MyAppSpec extends PlaySpec with GuiceOneAppPerSuite { override def fakeApplication(): Application = MyApp.build() }
    * }}}
    */
  def build(): Application = new GuiceApplicationBuilder(environment).build()
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

  def run[T](
    app: Application,
    shutdownDuration: FiniteDuration,
  )(f: Application => T): T =
    try {
      try {
        f(app)
      } finally {
        Await.result(app.stop(), shutdownDuration)
        ()
      }
    } catch {
      case NonFatal(t) =>
        t.printStackTrace()
        throw t
    }
}
