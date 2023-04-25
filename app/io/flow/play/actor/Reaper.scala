package io.flow.play.actor

import akka.actor.{ActorSystem, Extension, ExtensionId, ExtensionIdProvider, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/** To have an ActorRef terminated early in the CoordinatedShutdown process:
  *
  * 1. Add the ActorRef to the set to be managed
  * {{{Reaper.get(system).watch(actorRef)}}}
  *
  * 2. Call {{{Reaper.get(system).reapAll()}}} during shutdown to wait for all actors to be terminated
  * This is done automatically by CoordinatedShutdownActorReaperModule if loaded.
  */
object Reaper extends ExtensionId[Reaper] with ExtensionIdProvider {
  override def lookup = Reaper

  override def createExtension(system: akka.actor.ExtendedActorSystem) = new Reaper(system)
}

final class Reaper private[actor] (system: ActorSystem) extends Extension {
  private[this] val reaper = system.actorOf(Props(classOf[ReaperActor]), ReaperActor.Name)

  def watch(ref: akka.actor.ActorRef): Unit = reaper ! ReaperActor.Watch(ref)

  def reapAll(): Future[akka.Done] = {
    implicit val timeout: Timeout = 60.seconds
    (reaper ? ReaperActor.Reap).mapTo[akka.Done]
  }
}