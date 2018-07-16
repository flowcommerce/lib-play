package io.flow.play.actors.proxy

import akka.actor.{ActorRef, Props}
import com.amazonaws.services.sqs.AmazonSQSAsync
import io.flow.util.Config
import play.api.libs.json.{Json, OFormat}

object ProxyEnvelope {
  implicit val format: OFormat[ProxyEnvelope] = Json.format[ProxyEnvelope]
}
case class ProxyEnvelope(senderActorPath: String, messageType: String, message: String)

object SQSProxyActor {

  def senderProps(receiverActorPath: String, sqs: AmazonSQSAsync, config: Config, serde: ProxySerde): Props = {
    Props(new SQSSenderProxyActor(receiverActorPath, sqs, config, serde))
  }

  def receiverProps(receiver: ActorRef, sqs: AmazonSQSAsync, config: Config, serde: ProxySerde): Props = {
    Props(new SQSReceiverProxyActor(receiver, sqs, config, serde))
  }

  private[proxy] def generateQueueUrl(receiverActorPath: String, config: Config): String = {
    s"${config.requiredString("io.flow.play.actors.proxy.service_name")}"
  }

}