package io.flow.play.writeables

import javax.inject.{Inject, Singleton}
import play.api.http.{DefaultWriteables, Writeable}
import play.api.libs.json.Json

@Singleton
class JsonThrowableWriteable @Inject()(writeables: DefaultWriteables) {

  implicit val writeableOf_JsonThrowable: Writeable[Throwable] =
    writeables.writeableOf_JsValue.map[Throwable] { throwable =>
      val message = Option(throwable.getMessage).getOrElse("""¯\_(ツ)_/¯""")
      Json.obj("status" -> "KO", "message" -> message)
    }

}

object JsonThrowableWriteable extends JsonThrowableWriteable(Writeable)
