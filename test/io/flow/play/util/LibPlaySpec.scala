package io.flow.play.util

import java.util.UUID

import com.typesafe.config.ConfigFactory
import io.flow.play.clients.MockConfig
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration

trait LibPlaySpec extends PlaySpec with GuiceOneAppPerSuite {
  def createTestId(): String = UUID.randomUUID.toString

  def createMockConfig() = MockConfig(DefaultConfig(ApplicationConfig(Configuration(ConfigFactory.empty()))))

}