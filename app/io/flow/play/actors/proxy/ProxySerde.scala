package io.flow.play.actors.proxy

trait ProxySerde {
  def serialize[T](msg: T): String
  def deserialise[T](msg: String, msgType: String): T
}
