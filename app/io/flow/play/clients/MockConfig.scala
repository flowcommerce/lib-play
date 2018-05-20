package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig}

@javax.inject.Singleton
case class MockConfig @javax.inject.Inject() (
  defaultConfig: DefaultConfig
) extends Config {

  private val values = scala.collection.mutable.Map[String, Any]()

  override def optionalList(name: String): Option[Seq[String]] = {
    values.get(name) match {
      case Some(v) => Some(v.asInstanceOf[Seq[String]])
      case None => defaultConfig.optionalList(name)
    }
  }

  def set(name: String, value: Seq[String]) {
    values += (name -> value)
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
