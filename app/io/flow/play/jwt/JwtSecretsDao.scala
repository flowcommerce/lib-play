package io.flow.play.jwt

import java.net.URI

import akka.stream.Materializer
import akka.stream.alpakka.s3.scaladsl.S3Client
import io.flow.play.util.Config
import io.flow.secret.internal.v0.models.SecretConfig
import io.flow.secret.internal.v0.models.json._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait JwtSecretsDao {

  def get: Future[SecretConfig]

}

@Singleton
class S3JwtSecretsDao @Inject() (
  config: Config,
  s3Client: S3Client
)(implicit ec: ExecutionContext, mat: Materializer) extends JwtSecretsDao {

  private val s3Uri = config.requiredString("jwt.secrets.s3.uri")
  private val (s3Bucket, s3Key) = S3JwtSecretsDao.getBucketAndKey(s3Uri).get

  override def get: Future[SecretConfig] = {

    val (source, _) = s3Client.download(s3Bucket, s3Key)
    source.runReduce(_ ++ _).map(s => Json.parse(s.utf8String).as[SecretConfig])
  }

}

object S3JwtSecretsDao {

  def getBucketAndKey(uri: String): Try[(String, String)] =
    Try(new URI(uri))
    .flatMap { uriUri =>
      if (uriUri.getScheme.toLowerCase == "s3")
        Success((uriUri.getHost, uriUri.getPath.substring(1)))
      else
        Failure(new IllegalArgumentException("s3 URI must have s3 scheme"))
    }

}
