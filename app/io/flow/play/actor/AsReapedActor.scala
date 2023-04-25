package io.flow.play.actor

import akka.actor.Actor

/** Mixin to have ActorRef added to set of actors to be terminated early during CoordinatedShutdown. */
trait AsReapedActor {
  this: Actor =>

  Reaper.get(context.system).watch(self)
}
