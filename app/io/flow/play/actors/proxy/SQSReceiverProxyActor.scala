package io.flow.play.actors.proxy

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.{MessageAction, SqsSourceSettings}
import akka.stream.alpakka.sqs.scaladsl.{SqsAckSink, SqsSource}
import com.amazonaws.services.sqs.AmazonSQSAsync
import io.flow.akka.SafeReceive
import io.flow.util.Config
import play.api.libs.json.Json

class SQSReceiverProxyActor(receiver: ActorRef, sqs: AmazonSQSAsync, config: Config, serde: ProxySerde) extends Actor with ActorLogging {
  // This binds the created stream to this actors lifecycle, (i.e. actor dies, stream dies)
  private implicit val mat: ActorMaterializer = ActorMaterializer()
  private implicit val sqsClient: AmazonSQSAsync = sqs
  private val QueueUrl = SQSProxyActor.generateQueueUrl(receiver.path.toStringWithoutAddress, config)

  private val Source = SqsSource(
    QueueUrl,
    settings = SqsSourceSettings(
      waitTimeSeconds = 20,
      maxBufferSize = 100,
      maxBatchSize = 10
    )
  )(sqs)

  override def preStart(): Unit = {
    Source.map { msg =>
      val envelope = Json.parse(msg.getBody).as[ProxyEnvelope]
      val message = envelope.message // de-serialise
      receiver ! serde.deserialise(message, envelope.messageType)
      (msg, MessageAction.Delete)
    }.runWith(SqsAckSink(QueueUrl))
  }

  override def receive: Receive = SafeReceive {
    case _ => // Do nothing for now
  }

}
