package io.flow.play

import akka.pattern.ask
import akka.util.Timeout
import io.flow.play.actor.ReaperActor
import io.flow.play.util.LibPlaySpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration._

class ActorReaperModuleSpec extends LibPlaySpec with BeforeAndAfterAll with Matchers {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new ActorReaperModule())
      .build()

  "ActorReaperModule" should {
    "register ReaperActor" in {
      val reaper = app.actorSystem.actorSelection("/user/" + ReaperActor.Name)
      implicit val timeout: Timeout = 3.seconds
      Await.result(reaper ? ReaperActor.Reap, timeout.duration) mustBe akka.Done
    }
  }
}
