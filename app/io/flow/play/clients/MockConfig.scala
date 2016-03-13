package io.flow.play.clients

import io.flow.play.util.Config

@javax.inject.Singleton
case class MockConfig @javax.inject.Inject() () extends Config {

  override def optionalString(name: String): Option[String] = {
    name match {
      case "JWT_SALT" => Some("test")
      case _ => None
    }
  }

}
