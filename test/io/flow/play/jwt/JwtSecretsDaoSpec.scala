package io.flow.play.jwt

import akka.stream.Materializer
import io.flow.play.aws.S3Util
import io.flow.play.clients.JwtModule
import io.flow.secret.internal.v0.models.json._
import io.flow.secret.internal.v0.models.{Secret, SecretConfig}
import org.joda.time.DateTime
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class JwtSecretsDaoSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll with BeforeAndAfterEach
  with ScalaFutures with S3Util {

  private val s3bucket = "bucket"
  private val s3key = "key"

  override def fakeApplication(): Application = {
    val builder = new GuiceApplicationBuilder()
    builder
      .overrides(new JwtModule(builder.environment, builder.configuration))
      .configure("jwt.secrets.s3.bucket" -> s3bucket, "jwt.secrets.s3.key" -> s3key)
      .build()
  }

  private implicit val ec: ExecutionContext = app.actorSystem.dispatcher
  private implicit val mat: Materializer = app.materializer

  private val dao = app.injector.instanceOf[S3JwtSecretsDao]

  override def beforeEach(): Unit = {
    super.beforeEach()
    s3MockClient.listBuckets().asScala.map(_.getName).filter(_ == s3bucket).foreach(s3MockClient.deleteBucket)
    s3MockClient.createBucket(s3bucket)
  }

  "S3JwtSecretsDao" should {

    "get secrets from s3" in {
      val secretsConfig = SecretConfig(
        secrets = Seq(Secret("id1", "key1", DateTime.now), Secret("id2", "key2", DateTime.now)),
        preferredSecretId = "id2"
      )
      s3MockClient.putObject(s3bucket, s3key, Json.toJson(secretsConfig).toString())

      dao.get.futureValue(Timeout(2.seconds)) mustBe secretsConfig
    }

    "fail if file cannot be found" in {
      s3MockClient.deleteBucket(s3bucket)
      dao.get.failed.futureValue mustBe a[akka.stream.alpakka.s3.S3Exception]
    }

    "fail if file content cannot be parsed" in {
      s3MockClient.putObject(s3bucket, s3key, "not even json")
      dao.get.failed.futureValue mustBe a[com.fasterxml.jackson.core.JsonParseException]
    }

  }

}
