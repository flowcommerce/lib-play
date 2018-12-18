package io.flow.play.actors

import akka.actor.ActorSystem
import io.flow.log.RollbarLogger
import io.flow.play.util.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Common utilities to help with scheduling of actors with intervals
  * coming from configuration file.
  */
@deprecated("Deprecated in favour of lib-akka Scheduler", "0.4.78")
trait Scheduler {

  def config: Config

  def logger: RollbarLogger

  private[this] def log: RollbarLogger = logger.withKeyValue("class", getClass.getName)

  /**
   * Helper to schedule a recurring interval based on a configuration
   * parameter.
   *
   * Example:
   *
   *   conf/base.conf:
   *     io.flow.delta.api.CheckProjects.seconds = 300
   *     io.flow.delta.api.CheckProjects.seconds_initial = 3
   *
   *   conf/api/actors/...
   *     scheduleRecurring("io.flow.delta.api.CheckProjects.seconds") {
   *       periodicActor ! PeriodicActor.Messages.CheckProjects
   *     }
   *
   * @param configName The name of the configuration parameter containing the number
   *        of seconds between runs. You can also optionally add a
   *        configuration parameter of the same name with "_inital"
   *        appended to set the initial interval if you wish it to be
   *        different.
   */
  def scheduleRecurring[T](
    system: ActorSystem,
    configName: String
  ) (
    f: => T
  ) (
    implicit ec: ExecutionContext
  ) {
    val seconds = config.requiredPositiveInt(configName)
    val initial = config.optionalPositiveInt(s"${configName}_initial").getOrElse(seconds)
    log.
      withKeyValue("config_name", configName).
      withKeyValue("initial_seconds", initial).
      withKeyValue("recurring_seconds", seconds).
      info("scheduleRecurring")
    system.scheduler.schedule(
      FiniteDuration(initial, SECONDS),
      FiniteDuration(seconds, SECONDS),
      new Runnable {
        def run(): Unit = f
      }
    )
  }

  def scheduleRecurringWithDefault[T](
    system: ActorSystem,
    configName: String,
    default: Int
  )(
    f: => T
  )(
    implicit ec: ExecutionContext
  ) {
    val seconds = config.optionalPositiveInt(configName).getOrElse(default)
    val initial = config.optionalPositiveInt(s"${configName}_initial").getOrElse(seconds)

    log.
      withKeyValue("config_name", configName).
      withKeyValue("initial_seconds", initial).
      withKeyValue("recurring_seconds", seconds).
      info("scheduleRecurringWithDefault")

    system.scheduler.schedule(
      FiniteDuration(initial, SECONDS),
      FiniteDuration(seconds, SECONDS),
      new Runnable {
        def run(): Unit = f
      }
    )
  }

}
