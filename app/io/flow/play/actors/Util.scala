package io.flow.play.actors

import akka.actor.ActorRef
import io.flow.play.util.DefaultConfig
import play.api.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Common utilities to help with logging, scheduling of actors.
  * 
  * This class requires configuration of the execution context called
  * 'io-flow-play-actors-context'. Example configuration for the play
  * project:
  * 
  *     io-flow-play-actors-context {
  *       fork-join-executor {
  *       parallelism-factor = 2.0
  *       parallelism-max = 200
  *     }
  */
trait Util {

  private[this] implicit val mainActorExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("io-flow-play-actors-context")

  /**
   * Helper to schedule a message to be sent on a recurring interval
   * based on a configuration parameter.
   *
   * @param configName The name of the configuration parameter containing the number
   *        of seconds between runs. You can also optionally add a configuration
   *        parameter of the same name with "_inital" appended to set the initial
   *        interval if you wish it to be different.
   */
  def scheduleRecurring[T](
    actor: ActorRef,
    configName: String,
    message: T
  ) {
    val seconds = DefaultConfig.requiredString(configName).toInt
    val initial = DefaultConfig.optionalString(s"${configName}_initial").map(_.toInt).getOrElse(seconds)
    Logger.info(s"scheduling a periodic message[$message]. Initial[$initial seconds], recurring[$seconds seconds]")
    Akka.system.scheduler.schedule(
      FiniteDuration(initial, SECONDS),
      FiniteDuration(seconds, SECONDS),
      actor, message
    )
  }

  def withErrorHandler[T](
    description: Any
  ) (
    f: => T
  ) {
    try {
      f
    } catch {
      case t: Throwable => {
        Logger.error(msg(s"$description: ${t}") , t)
      }
    }
  }

  def withVerboseErrorHandler[T](
    description: Any
  ) (
    f: => T
  ) {
    Logger.info(msg(description.toString))
    withErrorHandler(description)(f)
  }

  def logUnhandledMessage[T](
    description: Any
  ) {
    Logger.error(msg(s"got an unhandled message: $description"))
  }

  private[this] def msg(value: String) = {
    s"${getClass.getName}: $value"
  }

}
