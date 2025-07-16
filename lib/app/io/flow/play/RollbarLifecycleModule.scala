package io.flow.play

import com.google.inject.Provider
import com.rollbar.notifier.Rollbar
import play.api.inject.{ApplicationLifecycle, Binding, Module}
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}

final class RollbarLifecycleModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[RollbarLifecycleModule.LifecycleHookRunner].toSelf.eagerly(),
    )
}

object RollbarLifecycleModule {
  import scala.concurrent.Future

  @Singleton
  final class LifecycleHookRunner @Inject() (
    rollbarProvider: Provider[Option[Rollbar]],
    applicationLifecycle: ApplicationLifecycle,
  ) {
    rollbarProvider.get().foreach { rollbar =>
      applicationLifecycle.addStopHook { () =>
        Future.successful {
          rollbar.info("Closing Rollbar")
          rollbar.close(true)
        }
      }
    }
  }
}
