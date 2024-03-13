package io.flow.play

import akka.pattern.ask
import akka.util.Timeout
import io.flow.akka.actor.{ManagedShutdownPhase, ReaperActor}
import io.flow.play.util.LibPlaySpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.Await
import scala.concurrent.duration._

class CoordinatedShutdownActorReaperModuleSpec extends LibPlaySpec with Matchers {

  "CoordinatedShutdownActorReaperModule" should {
    def verify(phase: ManagedShutdownPhase) = {
      // Look it up this way to verify that the module registered it (via its extension loaded in test.conf)
      val reaper = app.actorSystem.actorSelection("/user/" + ReaperActor.name(phase))
      implicit val timeout: Timeout = 3.seconds
      Await.result(reaper ? ReaperActor.Reap, timeout.duration) mustBe akka.Done
    }

    "register ReaperActor for all ManagedShutdownPhases" in {
      ManagedShutdownPhase.All.foreach(verify)
    }
  }
}
