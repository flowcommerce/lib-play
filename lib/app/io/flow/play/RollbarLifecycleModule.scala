package io.flow.play

import akka.actor.{ActorSystem, CoordinatedShutdown}
import com.google.inject.Provider
import com.rollbar.notifier.Rollbar
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future, blocking}

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
    rollbarProvider: Provider[Option[Rollbar]],
  ) {
    def closeRollbar(): Unit =
      rollbarProvider.get().foreach { rollbar =>
        rollbar.info("Closing Rollbar")
        rollbar.close(true)
      }

    CoordinatedShutdown
      .get(system)
      .addTask(
        phase = CoordinatedShutdown.PhaseActorSystemTerminate, // latest possible
        taskName = s"rollbar-close",
      ) { () =>
        implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
        Future(blocking(closeRollbar())).map(_ => akka.Done)
      }
  }
}
