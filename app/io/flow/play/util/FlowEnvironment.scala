package io.flow.play.util

sealed trait FlowEnvironment

object FlowEnvironment {

  case object Development extends FlowEnvironment { override def toString = "development" }
  case object Production extends FlowEnvironment { override def toString = "production" }

  val all = Seq(Development, Production)

  private[this] val byName = all.map(x => x.toString.toLowerCase -> x).toMap

  def fromString(value: String): Option[FlowEnvironment] = byName.get(value.toLowerCase)

}
