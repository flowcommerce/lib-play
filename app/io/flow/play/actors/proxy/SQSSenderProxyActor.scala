package io.flow.play.actors.proxy

import akka.actor.{Actor, ActorLogging}
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.SendMessageRequest
import io.flow.akka.SafeReceive
import io.flow.util.Config
import play.api.libs.json.Json

class SQSSenderProxyActor(receiverActorPath: String, sqs: AmazonSQSAsync, config: Config, serde: ProxySerde) extends Actor with ActorLogging {

  private val QueueUrl = SQSProxyActor.generateQueueUrl(receiverActorPath, config)

  override def receive: Receive = SafeReceive {
    case msg => sqs.sendMessage(new SendMessageRequest()
      .withQueueUrl(QueueUrl)
      .withMessageBody(Json.stringify(Json.toJson(ProxyEnvelope(
        senderActorPath = sender().path.toStringWithoutAddress,
        messageType = msg.getClass.getName,
        message = serde.serialize(msg)
      )))))
  }
}
