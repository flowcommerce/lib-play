package io.flow.play.actors

import akka.actor.ActorSystem
import io.flow.play.util.Config
import play.api.Logger
import play.api.Play.current
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Common utilities to help with scheduling of actors with intervals
  * coming from configuration file.
  */
trait Scheduler {

  def config: Config

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
    val seconds = config.requiredString(configName).toInt
    val initial = config.optionalString(s"${configName}_initial").map(_.toInt).getOrElse(seconds)
    Logger.info(s"[${getClass.getName}] scheduleRecurring[$configName]: Initial[$initial seconds], recurring[$seconds seconds]")
    system.scheduler.schedule(
      FiniteDuration(initial, SECONDS),
      FiniteDuration(seconds, SECONDS),
      new Runnable {
        def run = f
      }
    )
  }

}
