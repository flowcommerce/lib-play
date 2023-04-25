package io.flow.play

import akka.actor.{ActorRef, ActorSystem, CoordinatedShutdown}
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.AbstractModule
import io.flow.log.RollbarLogger
import io.flow.play.actor.ReaperActor
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.Configuration

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Named, Singleton}

/**
 * This module is used to perform early termination of actors.  It registers a ReaperActor, which
 * actors interested in being terminated early in the CoordinatedShutdown process can register with.
 *
 * The phase in which actors are terminated is CoordinatedShutdown.PhaseServiceRequestsDone. This was
 * chosen as it occurs before CoordinatedShutdown.PhaseServiceStop which Play's ApplicationLifecycle
 * runs in.  The default database module shuts down the database in ApplicationLifecycle.stop so the
 * idea here is to stop any actors that may be using the database.  These primarily include kinesis event
 * processors and journal actors.
 */
final class ActorReaperModule extends AbstractModule with AkkaGuiceSupport with ScalaModule {
  override def configure(): Unit = {
    bindActor[ReaperActor](name = ReaperActor.Name)
    bind[ActorReaper].asEagerSingleton()
  }
}

@Singleton
private[play] final class ActorReaper @Inject() (
  @Named("flow-reaper-actor") reaperActor: ActorRef,
  system: ActorSystem,
  config: Configuration,
  rollbar: RollbarLogger
) {
  @Inject
  def initialize(): Unit = {
    val phase = CoordinatedShutdown.PhaseServiceRequestsDone
    val timeout: Timeout = {
      val configPath = s"akka.coordinated-shutdown.phases.$phase.timeout"
      if (config.has(configPath)) {
        akka.util.Timeout(config.getMillis(configPath), TimeUnit.MILLISECONDS)
      } else {
        akka.util.Timeout(60 * 1000, TimeUnit.MILLISECONDS)
      }
    }

    val logger = rollbar.fingerprint("FlowActorsShutdown")
    implicit val ec = system.dispatcher

    CoordinatedShutdown
      .get(system)
    .addTask(phase, taskName = s"flow-wait-for-reaped-actors") { () =>
      logger.info("Waiting for watched actors to stop")
      reaperActor
        .ask(ReaperActor.Reap)(timeout)
        .map { _ =>
          logger.info("All watched actors stopped")
          akka.Done
        }
    }
  }
}
