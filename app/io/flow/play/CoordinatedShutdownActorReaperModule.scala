package io.flow.play

import akka.actor.{ActorSystem, CoordinatedShutdown}
import io.flow.log.RollbarLogger
import io.flow.play.actor.Reaper
import play.api.inject.Module
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}

@Singleton
final class CoordinatedShutdownActorReaperModule extends Module {
  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[CoordinatedShutdownActorReaper].toSelf.eagerly()
    )
  }
}

@Singleton
private[play] final class CoordinatedShutdownActorReaper @Inject() (
  system: ActorSystem,
  rollbar: RollbarLogger
) {
  @Inject
  def initialize(): Unit = {
    val logger = rollbar.fingerprint("CoordinatedShutdownActorReaper")
    val reaper = Reaper.get(system)
    implicit val ec = system.dispatcher

    CoordinatedShutdown
      .get(system)
      .addTask(CoordinatedShutdown.PhaseServiceRequestsDone, taskName = s"flow-cs-reap-actors") { () =>
        logger.info("Waiting for watched actors to stop")
        reaper.reapAsync().map { _ =>
          logger.info("All watched actors stopped")
          akka.Done
        }
      }
  }
}
