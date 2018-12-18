package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig}

import scala.collection.mutable

@javax.inject.Singleton
class MockConfig @javax.inject.Inject() (
  defaultConfig: DefaultConfig
) extends Config {

  override def optionalMap(name: String): Option[Map[String, Seq[String]]] = {
    values.get(name).map {
      _.asInstanceOf[Map[String, Seq[String]]]
    }.orElse(defaultConfig.optionalMap(name))
  }

  override def optionalList(name: String): Option[Seq[String]] = {
    values.get(name) match {
      case Some(v) => Some(v.asInstanceOf[Seq[String]])
      case None => defaultConfig.optionalList(name)
    }
  }

  def set(name: String, value: Seq[String]): Unit = {
    values += (name -> value)
  }

  val values: mutable.Map[String, Any] = {
    val d = scala.collection.mutable.Map[String, Any]()
    d += ("JWT_SALT" -> "test")
    d
  }

  def set(name: String, value: String): Unit = {
    values += (name -> value)
  }

  override def get(name: String): Option[String] = {
    values.get(name) match {
      case Some(v) => Some(v.toString)
      case None => defaultConfig.get(name)
    }
  }

}
