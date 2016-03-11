package io.flow.play.controllers

import io.flow.play.util.Config

case class MockConfig() extends Config {
  override def optionalString(name: String): Option[String] = {
    name match {
      case "JWT_SALT" => Some("test")
      case _ => None
    }
  }
}
