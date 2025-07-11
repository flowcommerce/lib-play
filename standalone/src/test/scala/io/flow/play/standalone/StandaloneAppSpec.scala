package io.flow.play.standalone

import org.scalatest.wordspec.AnyWordSpec
import play.api.inject.{ApplicationLifecycle, Injector}

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Future

class StandaloneAppSpec extends AnyWordSpec {
  "main passes args to run" in {
    val flag = new AtomicBoolean(false)
    val app = new StandaloneApp {
      override def run(args: Array[String])(implicit injector: Injector): Unit = {
        val result = args.headOption.flatMap(_.toBooleanOption).getOrElse(false)
        flag.set(result)
      }
    }
    app.main(Array("true"))
    assert(flag.get())
  }

  "does not swallow exception from run" in {
    val ex = intercept[RuntimeException] {
      val app = new StandaloneApp {
        override def run(args: Array[String])(implicit injector: Injector): Unit = sys.error("boom!")
      }
      app.main(Array.empty)
    }
    assert(ex.getMessage == "boom!")
  }

  "application lifecycle" in {
    val shutdown = new AtomicBoolean(false)
    val app = new StandaloneApp {
      override def run(args: Array[String])(implicit injector: Injector): Unit = {
        inject[ApplicationLifecycle].addStopHook(() => Future.successful(shutdown.set(true)))
      }
    }
    app.main(Array.empty)
    assert(shutdown.get())
  }
}
