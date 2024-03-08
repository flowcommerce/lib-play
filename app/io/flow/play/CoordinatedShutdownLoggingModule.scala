package io.flow.play

import akka.actor.CoordinatedShutdown
import io.flow.log.RollbarLogger
import play.api.inject.Module
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.MapHasAsScala

@Singleton
final class CoordinatedShutdownLoggingModule extends Module {
  def bindings(env: Environment, conf: Configuration) = {
    Seq(
      bind[CoordinatedShutdownLogger].toSelf.eagerly(),
    )
  }
}

@Singleton
final class CoordinatedShutdownLogger @Inject() (
  cs: CoordinatedShutdown,
  rollbar: RollbarLogger,
) {

  @Inject
  def addTasks(config: Configuration): Unit = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    val logger = rollbar.fingerprint(getClass.getSimpleName)

    getShutdownPhases(config).foreach { phase =>
      cs.addTask(phase, taskName = s"flow-coordinated-shutdown-log-$phase") { () =>
        Future(logger.info(s"phase $phase: in progress"))
          .map { _ =>
            logger.info(s"phase $phase: completed")
            akka.Done
          }
      }
    }
    cs.addJvmShutdownHook(logger.info("In Jvm shutdown hook"))
  }

  private[this] def getShutdownPhases(config: Configuration): Seq[String] = {
    // See CoordinatedShutdown.phasesFromConfig
    val phasesConf = config.underlying.getConfig("akka.coordinated-shutdown.phases")
    phasesConf.root.unwrapped.asScala.keys.toSeq
  }
}
