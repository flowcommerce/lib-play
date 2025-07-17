package io.flow.play

import akka.actor.{ActorSystem, CoordinatedShutdown}
import io.flow.log.RollbarLogger
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{Future, blocking}

final class RollbarLifecycleModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[RollbarLifecycleModule.LifecycleHookRunner].toSelf.eagerly(),
    )
}

private object RollbarLifecycleModule {
  @Singleton
  final class LifecycleHookRunner @Inject() (
    system: ActorSystem,
    logger: RollbarLogger,
  ) {
    def closeRollbar(): Unit = {
      logger.info("Closing Rollbar")
      logger.rollbar.foreach { rollbar =>
        rollbar.close(true)
      }
    }

    CoordinatedShutdown
      .get(system)
      .addTask(
        phase = CoordinatedShutdown.PhaseBeforeActorSystemTerminate, // latest possible
        taskName = s"rollbar-close",
      ) { () =>
        implicit val ec = system.dispatcher
        Future(blocking(closeRollbar()))
          .map(_ => akka.Done)
      }
  }
}
