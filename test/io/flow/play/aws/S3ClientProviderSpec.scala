package io.flow.play.aws

import akka.stream.Materializer
import akka.stream.alpakka.s3.scaladsl.S3Client
import akka.stream.scaladsl.Source
import akka.util
import akka.util.ByteString
import io.flow.play.clients.JwtModule
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class S3ClientProviderSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll with DefaultAwaitTimeout
  with FutureAwaits with S3Util {

  private implicit val ec: ExecutionContext = app.actorSystem.dispatcher
  private implicit val mat: Materializer = app.materializer

  /**
    * @see [[S3ClientProvider]] and [[JwtModule]]: a [[akka.stream.alpakka.s3.Proxy]] is provided in the test environment
    */
  private val s3Client = app.injector.instanceOf[S3Client]

  "S3ClientProvider" should {

    "provide a working client" in {
      val content = "content of the file"
      await {
        Source.single(ByteString(content)).runWith(s3Client.multipartUpload(testBucket, "f.txt"))
      } (util.Timeout(2.seconds))

      s3MockClient.getObjectAsString(testBucket, "f.txt") mustBe content
    }

  }

}
