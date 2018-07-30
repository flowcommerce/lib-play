package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig}

@javax.inject.Singleton
class MockConfig @javax.inject.Inject() (
  defaultConfig: DefaultConfig
) extends Config {

  override def optionalList(name: String): Option[Seq[String]] = {
    values.get(name) match {
      case Some(v) => Some(v.asInstanceOf[Seq[String]])
      case None => defaultConfig.optionalList(name)
    }
  }

  def set(name: String, value: Seq[String]) {
    values += (name -> value)
  }

  val values = {
    val d = scala.collection.mutable.Map[String, Any]()
    d += ("JWT_SALT" -> "test")
    d
  }

  def set(name: String, value: String) {
    values += (name -> value)
  }

  override def get(name: String): Option[String] = {
    values.get(name) match {
      case Some(v) => Some(v.toString)
      case None => defaultConfig.get(name)
    }
  }

}
