package io.flow.play.writeables

import javax.inject.{Inject, Singleton}
import play.api.http.{DefaultWriteables, Writeable}
import play.api.libs.json.{Json, Writes}

@Singleton
class JsonWritesWriteable @Inject()(writeables: DefaultWriteables) {

  implicit def writeableOf_JsonWrites[A: Writes]: Writeable[A] =
    writeables.writeableOf_JsValue.map[A](Json.toJson)

}

object JsonWritesWriteable extends JsonThrowableWriteable(Writeable)
