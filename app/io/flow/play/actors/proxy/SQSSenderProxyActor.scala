package io.flow.play.actors.proxy

import akka.actor.{Actor, ActorLogging}
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.SendMessageRequest
import io.flow.akka.SafeReceive
import play.api.libs.json.Json

class SQSSenderProxyActor(receiverActorName: String, sqs: AmazonSQSAsync, serviceName: String, serde: ProxySerde) extends Actor with ActorLogging {

  private val QueueUrl = SQSProxyActor.generateQueueUrl(receiverActorName, serviceName, sqs)

  override def receive: Receive = SafeReceive {
    case msg => sqs.sendMessage(new SendMessageRequest()
      .withQueueUrl(QueueUrl)
      .withMessageBody(Json.stringify(Json.toJson(ProxyEnvelope(
        senderActorPath = sender().path.toStringWithoutAddress,
        messageType = msg.getClass.getName,
        message = serde.serialize(msg)
      )))))
      ()
  }
}
