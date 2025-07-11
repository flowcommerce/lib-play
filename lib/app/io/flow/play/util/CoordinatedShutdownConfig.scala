package io.flow.play.util

import com.typesafe.config.ConfigFactory
import io.flow.play.util.CoordinatedShutdownConfig.PhaseConfig
import play.api.Configuration

import java.util.concurrent.TimeUnit.MILLISECONDS
import scala.concurrent.duration.{DurationLong, FiniteDuration}

case class CoordinatedShutdownConfig(phases: Seq[PhaseConfig]) {
  def phase(key: String): Option[PhaseConfig] = phases.find(_.key == key)
}

object CoordinatedShutdownConfig {
  case class PhaseConfig(
    key: String,
    dependsOn: Set[String],
    timeout: FiniteDuration,
    recover: Boolean,
    enabled: Boolean,
  )

  def apply(rootConfig: Configuration): CoordinatedShutdownConfig = {
    import scala.jdk.CollectionConverters._

    // See akka.actor.CoordinatedShutdown.phasesFromConfig
    val conf = rootConfig.underlying.getConfig("akka.coordinated-shutdown")
    val defaultPhaseTimeout = conf.getString("default-phase-timeout")
    val phasesConf = conf.getConfig("phases")
    val defaultPhaseConfig = ConfigFactory.parseString(s"""
      timeout = $defaultPhaseTimeout
      recover = true
      enabled = true
      depends-on = []
    """)
    val phases = phasesConf.root.unwrapped.asScala.toSeq.map {
      case (phaseKey, _: java.util.Map[_, _]) =>
        val c = phasesConf.getConfig(phaseKey).withFallback(defaultPhaseConfig)
        val dependsOn = c.getStringList("depends-on").asScala.toSet
        val timeout = c.getDuration("timeout", MILLISECONDS).millis
        val recover = c.getBoolean("recover")
        val enabled = c.getBoolean("enabled")
        PhaseConfig(phaseKey, dependsOn, timeout, recover, enabled)
      case (k, v) =>
        sys.error(s"CoordinatedShutdownConfig: Expected object value for [$k], got [$v]")
    }
    CoordinatedShutdownConfig(phases)
  }
}
