package io.flow.play

import akka.pattern.ask
import akka.util.Timeout
import io.flow.akka.actor.{ManagedShutdownPhase, ReaperActor}
import io.flow.play.util.LibPlaySpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration._

class CoordinatedShutdownActorReaperModuleSpec extends LibPlaySpec with BeforeAndAfterAll with Matchers {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new CoordinatedShutdownActorReaperModule())
      .build()

  "CoordinatedShutdownActorReaperModule" should {
    def verify(phase: ManagedShutdownPhase) = {
      // Look it up this way to verify that the module registered it (via its extension)
      val reaper = app.actorSystem.actorSelection("/user/" + ReaperActor.name(phase))
      implicit val timeout: Timeout = 3.seconds
      Await.result(reaper ? ReaperActor.Reap, timeout.duration) mustBe akka.Done
    }

    "register ReaperActor for all ManagedShutdownPhases" in {
      ManagedShutdownPhase.All.foreach(verify)
    }
  }
}
