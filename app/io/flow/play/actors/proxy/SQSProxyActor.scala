package io.flow.play.actors.proxy

import akka.actor.{ActorRef, Props}
import com.amazonaws.services.sqs.AmazonSQSAsync
import play.api.libs.json.{Json, OFormat}

object ProxyEnvelope {
  implicit val format: OFormat[ProxyEnvelope] = Json.format[ProxyEnvelope]
}
case class ProxyEnvelope(senderActorPath: String, messageType: String, message: String)

object SQSProxyActor {

  def senderProps(receiverActorName: String, serviceName: String, sqs: AmazonSQSAsync, serde: ProxySerde): Props = {
    Props(new SQSSenderProxyActor(receiverActorName, sqs, serviceName, serde))
  }

  def receiverProps(serviceName: String, receiver: ActorRef, sqs: AmazonSQSAsync, serde: ProxySerde): Props = {
    Props(new SQSReceiverProxyActor(receiver, sqs, serviceName, serde))
  }

  private[proxy] def generateQueueUrl(receiverActorName: String, serviceName: String, sqs: AmazonSQSAsync): String = {
    val queueName = s"$serviceName-$receiverActorName"
    sqs.createQueue(queueName).getQueueUrl
  }

}