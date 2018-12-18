package io.flow.play.actors.proxy

import java.lang.reflect.Method

import akka.actor.{ActorRef, ActorSystem}
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.github.ghik.silencer.silent
import com.google.inject.name.Names
import com.google.inject.util.Providers
import com.google.inject.{AbstractModule, Binder}
import javax.inject.{Inject, Provider}
import play.api.inject.{BindingKey, Injector}
import play.api.libs.concurrent.AkkaGuiceSupport

trait ActorProxyGuiceSupport {
  self: AbstractModule with AkkaGuiceSupport =>

  protected val serviceName: String

  private def accessBinder: Binder = {
    val method: Method = classOf[AbstractModule].getDeclaredMethod("binder")
    if (!method.isAccessible) {
      method.setAccessible(true)
    }
    method.invoke(this).asInstanceOf[Binder]
  }

  def bindActorProxySender(name: String, serde: ProxySerde): Unit = {
    accessBinder.bind(classOf[ActorRef])
      .annotatedWith(Names.named(name))
      .toProvider(Providers.guicify(new SenderProxyActorProvider(name, serde)))
      .asEagerSingleton()
  }

  def bindActorProxyReceiver(name: String, proxiedName: String, serde: ProxySerde): Unit = {
    accessBinder.bind(classOf[ActorRef])
      .annotatedWith(Names.named(name))
      .toProvider(Providers.guicify(new ReceiverProxyActorProvider(name, proxiedName, serde)))
      .asEagerSingleton()
  }

  @silent private class SenderProxyActorProvider(name: String, serde: ProxySerde) extends Provider[ActorRef] {

    @Inject private var actorSystem: ActorSystem = _
    @Inject private var injector: Injector = _
    lazy val get = {
      val sqs = injector.instanceOf(classOf[AmazonSQSAsync])
      actorSystem.actorOf(SQSProxyActor.senderProps(
        receiverActorName = name,
        serviceName = serviceName,
        sqs = sqs,
        serde = serde
      ), name)
    }
  }

  @silent private class ReceiverProxyActorProvider(name: String, proxiedName: String, serde: ProxySerde) extends Provider[ActorRef] {

    @Inject private var actorSystem: ActorSystem = _
    @Inject private var injector: Injector = _
    lazy val get = {
      val proxiedActorRef = injector.instanceOf(BindingKey(classOf[ActorRef]).qualifiedWith(proxiedName))
      val sqs = injector.instanceOf(classOf[AmazonSQSAsync])
      actorSystem.actorOf(SQSProxyActor.receiverProps(
        serviceName = serviceName,
        receiver = proxiedActorRef,
        sqs = sqs,
        serde = serde
      ), name)
    }
  }

}
