package io.flow.play.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Terminated}

import javax.inject.Singleton
import scala.collection.mutable.{ListBuffer => MutableListBuffer}

object ReaperActor {
  val Name: String = "flow-reaper-actor"

  case class Watch(ref: ActorRef)
  case object Reap
}

/**
 * Actor that watches other actors and sends them a PoisonPill when it receives a Reap message.
 * Intended for use in graceful shutdown with CoordinatedShutdown.
 */
@Singleton
final class ReaperActor extends Actor with ActorLogging {
  private[this] val watched = MutableListBuffer.empty[ActorRef]
  private[this] var stopSent: Boolean = false

  override def receive: Receive = {
    case ReaperActor.Watch(ref) =>
      context.watch(ref)
      watched += ref
      log.info(s"Watching ${ref.path}")

    case ReaperActor.Reap =>
      if (watched.isEmpty) {
        log.info(s"All watched actors stopped")
        stopSent = false // for re-use within tests
        sender() ! akka.Done
      } else {
        if (!stopSent) {
          log.info(s"Sending stop to all (${watched.size}) watched actors")
          watched.foreach { ref =>
            ref ! PoisonPill // Allow actors to process all messages in mailbox before stopping
          }
          stopSent = true
        }
        self forward ReaperActor.Reap
      }

    case Terminated(ref) =>
      watched -= ref
      log.info(s"Stopped watching ${ref.path}")
  }
}
