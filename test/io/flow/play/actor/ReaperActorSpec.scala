package io.flow.play.actor

import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.concurrent.atomic.AtomicLong

object ReaperActorSpec {
  case class SleepFor(millis: Long)

  class SleepyActor(accumulator: AtomicLong) extends Actor with ActorLogging {
    override def receive = {
      case SleepFor(millis) =>
        val cumulativeMillis = accumulator.addAndGet(millis)
        log.info(s"Sleeping for $cumulativeMillis ms")
        Thread.sleep(cumulativeMillis)
    }
  }
}


class ReaperActorSpec extends TestKit(ActorSystem("ReaperActorSpec")) with ImplicitSender
  with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  import ReaperActorSpec._

  override def beforeAll(): Unit = {
    system.actorOf(Props(classOf[ReaperActor]), ReaperActor.Name)
    ()
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private[this] def reaper: ActorSelection =
    system.actorSelection("/user/" + ReaperActor.Name)

  "ReaperActor" must {
    "terminate watched actors" in {
      val echo = system.actorOf(TestActors.echoActorProps)
      reaper ! ReaperActor.Watch(echo)
      val probe = TestProbe()
      probe.watch(echo)

      echo ! "hello"
      expectMsg("hello")
      reaper ! ReaperActor.Reap
      probe.expectTerminated(echo)
      expectMsg(akka.Done) // from reaper when all watched actors have terminated
    }
  }

  "allow all messages in watched actors to process" in {
    val accumulator = new AtomicLong(0)
    val sleeper = system.actorOf(Props(new SleepyActor(accumulator)))
    reaper ! ReaperActor.Watch(sleeper)
    val probe = TestProbe()
    probe.watch(sleeper)

    val messages = Seq(SleepFor(10), SleepFor(20), SleepFor(30), SleepFor(40), SleepFor(50))
    messages.foreach { message =>
      sleeper ! message
    }
    reaper ! ReaperActor.Reap
    probe.expectTerminated(sleeper)
    expectMsg(akka.Done) // from reaper when all watched actors have terminated
    accumulator.get() mustBe messages.map(_.millis).sum
  }
}
