package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig}

@javax.inject.Singleton
case class MockConfig @javax.inject.Inject() (
  defaultConfig: DefaultConfig
) extends Config {

  val values = {
    val d = scala.collection.mutable.Map[String, String]()
    d += ("JWT_SALT" -> "test")
    d
  }

  def set(name: String, value: String) {
    values += (name -> value)
  }

  override def get(name: String): Option[String] = {
    values.get(name) match {
      case Some(v) => Some(v)
      case None => defaultConfig.get(name)
    }
  }

}
