package io.flow.play

import akka.actor.{ActorSystem, CoordinatedShutdown}
import io.flow.akka.actor.{ManagedShutdownPhase, Reaper}
import io.flow.log.RollbarLogger
import io.flow.play.util.CoordinatedShutdownConfig
import io.flow.play.util.CoordinatedShutdownConfig.PhaseConfig
import play.api.inject.Module
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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
  private[this] val logger: RollbarLogger = rollbar.fingerprint("CoordinatedShutdownActorReaper")
  private[this] val coordinatedShutdown: CoordinatedShutdown = CoordinatedShutdown.get(system)
  private[this] val reaper: Reaper = Reaper.get(system)

  @Inject
  def initialize(config: Configuration): Unit = {
    coordinatedShutdown.addJvmShutdownHook(logger.info("In Jvm shutdown hook"))

    CoordinatedShutdownConfig(config).phases.foreach { phaseConfig =>
      managedShutdownPhase(phaseConfig.key) match {
        case Some(managedPhase) => addShutdownReaperTask(phaseConfig, managedPhase)
        case None => addShutdownLoggingTask(phaseConfig)
      }
    }
  }

  private[this] def addShutdownReaperTask(phaseConfig: PhaseConfig, managedPhase: ManagedShutdownPhase): Unit =
    coordinatedShutdown.addTask(
      phase = phaseConfig.key,
      taskName = s"flow-coordinated-shutdown-${phaseConfig.key}",
    ) { () =>
      implicit val ec: ExecutionContext = system.dispatcher
      val logger = this.logger.withKeyValue("phase", phaseConfig.key)
      logger.info(s"${phaseConfig.key}: waiting for watched actors to stop")

      reaper.reapAsync(managedPhase)(phaseConfig.timeout).map { _ =>
        logger.info(s"${phaseConfig.key}: all watched actors stopped")
        akka.Done
      }
    }

  private[this] def addShutdownLoggingTask(phaseConfig: PhaseConfig): Unit =
    coordinatedShutdown.addTask(
      phase = phaseConfig.key,
      taskName = s"flow-coordinated-shutdown-${phaseConfig.key}",
    ) { () =>
      implicit val ec: ExecutionContext = system.dispatcher
      val logger = this.logger.withKeyValue("phase", phaseConfig.key)

      // Logging may be somewhat misleading in that this task in the phase has completed, not the phase.
      Future(logger.info(s"${phaseConfig.key}: task in progress"))
        .map { _ =>
          logger.info(s"${phaseConfig.key}: task completed")
          akka.Done
        }
    }

  private[this] def managedShutdownPhase(key: String): Option[ManagedShutdownPhase] =
    key match {
      case CoordinatedShutdown.PhaseServiceUnbind => Some(ManagedShutdownPhase.ServiceUnbind)
      case CoordinatedShutdown.PhaseServiceRequestsDone => Some(ManagedShutdownPhase.ServiceRequestsDone)
      case CoordinatedShutdown.PhaseServiceStop => Some(ManagedShutdownPhase.ServiceStop)
      case _ => None
    }
}
