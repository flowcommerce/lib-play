package io.flow.play.actors.proxy

trait ProxySerde {
  def serialize(msg: Any): String
  def deserialise(msg: String, msgType: String): Any
}
