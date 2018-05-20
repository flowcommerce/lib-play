package io.flow.play.jwt

import java.net.URISyntaxException

import akka.stream.Materializer
import io.flow.play.aws.S3Util
import io.flow.secret.internal.v0.models.json._
import io.flow.secret.internal.v0.models.{Secret, SecretConfig}
import org.joda.time.DateTime
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, TryValues}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class JwtSecretsDaoSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll with BeforeAndAfterEach
  with ScalaFutures with S3Util with TryValues {

  private val s3Uri = "s3://bucket/key"
  private val (s3Bucket, s3Key) = S3JwtSecretsDao.getBucketAndKey(s3Uri).get

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().configure("jwt.secrets.s3.uri" -> s3Uri).build()
  }

  private implicit val ec: ExecutionContext = app.actorSystem.dispatcher
  private implicit val mat: Materializer = app.materializer

  private val dao = app.injector.instanceOf[S3JwtSecretsDao]

  override def beforeEach(): Unit = {
    super.beforeEach()
    s3MockClient.listBuckets().asScala.map(_.getName).filter(_ == s3Bucket).foreach(s3MockClient.deleteBucket)
    s3MockClient.createBucket(s3Bucket)
  }

  "S3JwtSecretsDao" should {

    "extract bucket and key from uri" in {
      val (bucket, key) = S3JwtSecretsDao.getBucketAndKey("s3://bucket/path/to/file.txt").success.value
      bucket mustBe "bucket"
      key mustBe "path/to/file.txt"
    }

    "fail to extract bucket and key if scheme is not s3" in {
      val failure = S3JwtSecretsDao.getBucketAndKey("http://bucket.s3.amazon.com/path/to/file.txt").failure
      failure.exception mustBe a[IllegalArgumentException]
    }

    "fail to extract bucket and key if uri is malformed" in {
      val failure = S3JwtSecretsDao.getBucketAndKey("not a valid uri").failure
      failure.exception mustBe a[URISyntaxException]
    }

    "get secrets from s3" in {
      val secretsConfig = SecretConfig(
        secrets = Seq(Secret("id1", "key1", DateTime.now), Secret("id2", "key2", DateTime.now)),
        preferredSecretId = "id2"
      )
      s3MockClient.putObject(s3Bucket, s3Key, Json.toJson(secretsConfig).toString())

      dao.get.futureValue(Timeout(2.seconds)) mustBe secretsConfig
    }

    "fail if file cannot be found" in {
      s3MockClient.deleteBucket(s3Bucket)
      dao.get.failed.futureValue mustBe a[akka.stream.alpakka.s3.S3Exception]
    }

    "fail if file content cannot be parsed" in {
      s3MockClient.putObject(s3Bucket, s3Key, "not even json")
      dao.get.failed.futureValue mustBe a[com.fasterxml.jackson.core.JsonParseException]
    }

  }

}
