package io.flow.play.aws

import akka.stream.alpakka.s3.Proxy
import com.amazonaws.auth.{AWSStaticCredentialsProvider, AnonymousAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import io.findify.s3mock.S3Mock
import org.scalatest.BeforeAndAfterAll

/**
  * Adds functionality to allow tests relying on s3:
  *  - starts and stops a s3 mock server (@see s3Mock)
  *  - creates a test bucket (@see testBucket)
  *  - provides a s3 client (@see s3MockClient)
  */
trait S3Util {
  this: BeforeAndAfterAll =>

  val testBucket = "test-bucket"

  private val s3Proxy = Proxy(host = "localhost", port = 9001, scheme = "http")

  protected val s3MockClient: AmazonS3 = AmazonS3ClientBuilder
    .standard
    .withPathStyleAccessEnabled(true)
    .withEndpointConfiguration(new EndpointConfiguration(s"${s3Proxy.scheme}://${s3Proxy.host}:${s3Proxy.port}", "us-west-2"))
    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
    .build

  private val s3Mock = S3Mock(port = s3Proxy.port)

  override def beforeAll(): Unit = {
    s3Mock.start
    s3MockClient.createBucket(testBucket)
  }

  override def afterAll(): Unit = s3Mock.stop

}
