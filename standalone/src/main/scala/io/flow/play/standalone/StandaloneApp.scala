package io.flow.play.standalone

import io.flow.log.{RollbarLogger, RollbarModule}
import io.flow.play.metrics.{MetricsModule, MetricsSystem}
import io.flow.util.FlowEnvironment
import play.api.db.{DBModule, HikariCPModule}
import play.api.inject.guice.{GuiceInjectorBuilder, GuiceableModule}
import play.api.inject.{ApplicationLifecycle, Injector}
import play.api.{Configuration, Environment, Mode}

import scala.annotation.nowarn
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag
import scala.util.control.NonFatal

// Subset of play.api.Application, no ActorSystem etc.
trait Application {
  def injector: Injector
}

/** Provides a foundation for an opinionated application that performs work and then terminates, as opposed to a
  * long-running Play service.
  *
  * This is not a full Play application, but it does support key Play features such as `ApplicationLifecycle`, Play
  * configuration, and Guice dependency injection. This should allow most of our normal service code to be used outside
  * of a Play service.
  *
  * Module support is provided, but modules must be explicitly specified via the `modules` method. Modules listed in
  * configuration files under the `play.modules.enabled` setting will not be loaded. This design choice helps avoid
  * unintended side effects in standalone applications—such as accidentally consuming Kinesis streams.
  *
  * A default set of modules is included to support common behaviors such as logging, metrics reporting, and database
  * access.
  *
  * Example usage:
  * {{{
  * object MyApp extends StandaloneApp {
  *   override def run(args: Array[String])(implicit app: Application): Unit = {
  *     val myObj = inject[MyObj]
  *     // do something
  *   }
  *   override def modules: Seq[GuiceableModule] = super.modules ++ Seq(...)
  * }
  * }}}
  */
trait StandaloneApp {
  def run(args: Array[String])(implicit app: Application): Unit

  final def main(args: Array[String]): Unit =
    StandaloneApp.withApplication(environment, modules) { app =>
      run(args)(app)
    }

  final def inject[T: ClassTag](implicit app: Application): T = app.injector.instanceOf[T]

  final def rollbar(implicit app: Application): RollbarLogger = inject[RollbarLogger]

  def modules: Seq[GuiceableModule] = StandaloneApp.DefaultModules

  def environment: Environment = StandaloneApp.DefaultEnvironment
}

object StandaloneApp {
  lazy val DefaultModules: Seq[GuiceableModule] = Seq(
    new Modules.PlayConfigurationModule(),
    new Modules.PlayApplicationLifecycleModule(),
    new Modules.SLFLoggerConfigurationModule(),
    new DBModule(),
    new HikariCPModule(),
    new Modules.FlowConfigurationModule(),
    new RollbarModule(),
    new MetricsModule(),
  )

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
    environment: Environment,
    modules: Seq[GuiceableModule],
  )(f: Application => T): T =
    try {
      val configuration = Configuration.load(
        classLoader = environment.classLoader,
        properties = System.getProperties,
        directSettings = Map.empty[String, String],
        allowMissingApplicationConf = true,
      )
      val _injector = new GuiceInjectorBuilder(environment, configuration, modules).build()
      val applicationLifecycle = _injector.instanceOf[ApplicationLifecycle]

      // Force a flush here if the application has not flushed logs yet. Rollbar by default flushes every 10 seconds.
      val logger = _injector.instanceOf[RollbarLogger]
      applicationLifecycle.addStopHook(() => Future.successful(logger.rollbar.foreach(_.close(true))))

      // DatadogMetricsSystem pushes a final report on close.
      val metricsSystem = _injector.instanceOf[MetricsSystem]
      applicationLifecycle.addStopHook(() => Future.successful(metricsSystem.close()))

      try {
        val app = new Application {
          override def injector: Injector = _injector
        }
        f(app)
      } finally {
        stopApp(applicationLifecycle)
      }
    } catch {
      case NonFatal(t) =>
        t.printStackTrace()
        throw t
    }

  @nowarn("msg=deprecated")
  private def stopApp(app: ApplicationLifecycle): Unit = {
    Await.result(app.stop(), 30.seconds)
    ()
  }
}
