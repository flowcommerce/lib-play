package io.flow.play.clients

import akka.stream.alpakka.s3.Proxy
import akka.stream.alpakka.s3.scaladsl.S3Client
import com.amazonaws.auth.AWSCredentialsProvider
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, TypeLiteral}
import io.flow.play.aws.{AwsFallbackCredentials, S3ClientProvider}
import io.flow.play.jwt._
import io.flow.play.util._
import io.flow.token.v0.interfaces.{Client => TokenClient}
import io.flow.user.v0.interfaces.{Client => UserClient}
import play.api.inject.Module
import play.api.{Configuration, Environment, Mode}

import scala.concurrent.duration.FiniteDuration

class ConfigModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(bind[Config].to[DefaultConfig])
      case Mode.Test => Seq(bind[Config].to[MockConfig])
    }
  }

}

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[Registry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development | FlowEnvironment.Workstation => Seq(
            bind[Registry].to[DevelopmentRegistry]
          )
        }
      }
      case Mode.Test => Seq(
        bind[Registry].to[MockRegistry]
      )
    }
  }

}

class TokenClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[TokenClient].to[DefaultTokenClient]
      )
      case Mode.Test => Seq(
        bind[TokenClient].to[MockTokenClient]
      )
    }
  }

}

class UserClientModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod | Mode.Dev => Seq(
        bind[UserClient].to[DefaultUserClient]
      )
      case Mode.Test => Seq(
        bind[UserClient].to[MockUserClient]
      )
    }
  }

}

class JwtModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    environment.mode match {
      case Mode.Prod | Mode.Dev =>

        // To ensure secrets are accessible, the retriever service is started eagerly so that the app would
        // fail to start if the secrets cannot be accessed.
        // Downside of this approach is that an app which would not have needed to read the secrets would also
        // fail to start
        bind(classOf[JwtSecretsRetrieverService]).to(classOf[RefreshingJwtSecretsRetrieverService]).asEagerSingleton()

        bind(classOf[FiniteDuration])
          .annotatedWith(Names.named("jwtSecretsReloadInterval"))
          .toInstance(configuration.get[FiniteDuration]("jwt.secrets.reload-interval"))
        bind(classOf[JwtSecretsDao]).to(classOf[S3JwtSecretsDao])
        bind(classOf[S3Client]).toProvider(classOf[S3ClientProvider])
        bind(classOf[AWSCredentialsProvider]).to(classOf[AwsFallbackCredentials])
        bind(new TypeLiteral[Option[Proxy]] {}).toInstance(None)

      case Mode.Test =>
        bind(classOf[JwtSecretsRetrieverService]).to(classOf[MockJwtSecretsRetrieverService])
        bind(classOf[S3Client]).toProvider(classOf[S3ClientProvider])
        // allows to test s3 locally
        bind(new TypeLiteral[Option[Proxy]] {}).toInstance(Some(Proxy(host = "localhost", port = 9001, scheme = "http")))
    }
  }

}
