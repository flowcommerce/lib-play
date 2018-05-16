package io.flow.play.aws

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.s3.scaladsl.S3Client
import akka.stream.alpakka.s3.{Proxy, S3Settings}
import com.amazonaws.auth.{AWSCredentialsProviderChain, _}
import com.amazonaws.regions.{AwsRegionProvider, AwsRegionProviderChain}
import io.flow.play.util.Config
import javax.inject.{Inject, Provider, Singleton}
import play.api.{Environment, Mode}

import scala.collection.JavaConverters._
import scala.util.Try

@Singleton
class S3ClientProvider @Inject() (
  aWSCredentials: AwsFallbackCredentials,
  s3Proxy: Option[Proxy],
  environment: Environment
)(implicit system: ActorSystem, mat: Materializer) extends Provider[S3Client] {

  private[this] val alpakkaS3Settings = {
    val settings = S3Settings().copy(credentialsProvider = aWSCredentials, pathStyleAccess = true, proxy = s3Proxy)

    // if in Test mode and a fall back to us-east-1 in case the original region provider does not provide any region
    val regionProvider =
      if (environment.mode == Mode.Test)
        new AwsRegionProviderChain(settings.s3RegionProvider, new AwsRegionProvider { val getRegion = "us-east-1" })
      else
        settings.s3RegionProvider

    settings.copy(s3RegionProvider = regionProvider)
  }
  private[this] val alpakkaS3Client = new S3Client(alpakkaS3Settings)

  override def get(): S3Client = alpakkaS3Client

}

@Singleton
class AwsFallbackCredentials @Inject()(
  awsEnvironmentVariables: AwsEnvironmentVariables
) extends AWSCredentialsProviderChain(
  Seq(
    for {
      accessKey <- awsEnvironmentVariables.awsAccessKey
      secretKey <- awsEnvironmentVariables.awsSecretKey
    } yield new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)),
    Some(new DefaultAWSCredentialsProviderChain())
  ).flatten.asJava
) {

  // don't fail, just use S3 anonymously
  override def getCredentials: AWSCredentials = Try(super.getCredentials).toOption.orNull

}

@Singleton
class AwsEnvironmentVariables @Inject() (config: Config, env: Environment) {

  private val DefaultPresignedUrlExpireDays = 7

  val awsAccessKey: Option[String] = config.optionalString("aws.access.key").orElse {
    env.mode match {
      case Mode.Test => Some("testAwsAccessKey")
      case _ => None
    }
  }

  val awsSecretKey: Option[String] = config.optionalString("aws.secret.key").orElse {
    env.mode match {
      case Mode.Test => Some("testAwsSecretKey")
      case _ => None
    }
  }

  val awsPresignedUrlExpireDays: Int =
    config.optionalPositiveInt("aws.presigned.url.expire.days").getOrElse(DefaultPresignedUrlExpireDays)

  val awsRegionProvider =

}
