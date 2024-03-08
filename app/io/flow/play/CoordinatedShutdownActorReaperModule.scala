package io.flow.play

import akka.actor.{ActorSystem, CoordinatedShutdown}
import io.flow.log.RollbarLogger
import io.flow.akka.actor.{ManagedShutdownPhase, Reaper}
import play.api.inject.Module
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}

@Singleton
final class CoordinatedShutdownActorReaperModule extends Module {
  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[CoordinatedShutdownActorReaper].toSelf.eagerly(),
    )
  }
}

@Singleton
private[play] final class CoordinatedShutdownActorReaper @Inject() (
  system: ActorSystem,
  rollbar: RollbarLogger,
) {
  @Inject
  def initialize(): Unit = {
    val reaper = Reaper.get(system)
    implicit val ec = system.dispatcher

    def coordinatedShutdownPhase(phase: ManagedShutdownPhase): String =
      phase match {
        case ManagedShutdownPhase.ServiceUnbind => CoordinatedShutdown.PhaseBeforeServiceUnbind
        case ManagedShutdownPhase.ServiceRequestsDone => CoordinatedShutdown.PhaseServiceRequestsDone
        case ManagedShutdownPhase.ServiceStop => CoordinatedShutdown.PhaseServiceStop
      }

    ManagedShutdownPhase.All.foreach { phase =>
      CoordinatedShutdown
        .get(system)
        .addTask(coordinatedShutdownPhase(phase), taskName = s"flow-coordinated-shutdown-$phase") { () =>
          val logger = rollbar.fingerprint("CoordinatedShutdownActorReaper").withKeyValue("phase", phase.toString)
          logger.info(s"phase $phase: waiting for watched actors to stop")
          reaper.reapAsync(phase).map { _ =>
            logger.info(s"phase $phase: all watched actors stopped")
            akka.Done
          }
        }
    }
  }
}
