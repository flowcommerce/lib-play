package io.flow.play.jwt

import akka.stream.Materializer
import akka.stream.alpakka.s3.scaladsl.S3Client
import io.flow.play.util.Config
import io.flow.secret.internal.v0.models.json._
import io.flow.secret.internal.v0.models.SecretConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

trait JwtSecretsDao {

  def get: Future[SecretConfig]

}

@Singleton
class S3JwtSecretsDao @Inject() (
  config: Config,
  s3Client: S3Client
)(implicit ec: ExecutionContext, mat: Materializer) extends JwtSecretsDao {

  private val s3Bucket = config.requiredString("jwt.secrets.s3.bucket")
  private val s3Key = config.requiredString("jwt.secrets.s3.key")

  override def get: Future[SecretConfig] = {
    val (source, _) = s3Client.download(s3Bucket, s3Key)
    source.runReduce(_ ++ _).map(s => Json.parse(s.utf8String).as[SecretConfig])
  }

}
