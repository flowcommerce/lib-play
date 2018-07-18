package io.flow.play.actors.proxy

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.{SqsAckSink, SqsSource}
import akka.stream.alpakka.sqs.{MessageAction, SqsSourceSettings}
import com.amazonaws.services.sqs.AmazonSQSAsync
import io.flow.akka.SafeReceive
import play.api.libs.json.Json

class SQSReceiverProxyActor(receiver: ActorRef, sqs: AmazonSQSAsync, serviceName: String, serde: ProxySerde) extends Actor with ActorLogging {
  // This binds the created stream to this actors lifecycle, (i.e. actor dies, stream dies)
  private implicit val mat: ActorMaterializer = ActorMaterializer()
  private implicit val sqsClient: AmazonSQSAsync = sqs
  private val QueueUrl = SQSProxyActor.generateQueueUrl(receiver.path.name, serviceName, sqs)

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
      receiver ! serde.deserialise(envelope.message, envelope.messageType)
      (msg, MessageAction.Delete)
    }.runWith(SqsAckSink(QueueUrl))
  }

  override def receive: Receive = SafeReceive {
    case msg => log.warning(s"UNKNOWN SQS message received: $msg")
  }

}
