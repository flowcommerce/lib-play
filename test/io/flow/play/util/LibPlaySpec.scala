package io.flow.play.util

import java.util.UUID

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

trait LibPlaySpec extends PlaySpec with GuiceOneAppPerSuite {
  def createTestId(): String = UUID.randomUUID.toString
}