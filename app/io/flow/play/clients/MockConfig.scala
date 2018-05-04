package io.flow.play.clients

import io.flow.play.util.{Config, DefaultConfig}

import scala.collection.mutable

@javax.inject.Singleton
case class MockConfig @javax.inject.Inject() (
  defaultConfig: DefaultConfig
) extends Config {

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
    mutable.Map(
      "JWT_SALTS" -> "salt1 salt2"
    )
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
