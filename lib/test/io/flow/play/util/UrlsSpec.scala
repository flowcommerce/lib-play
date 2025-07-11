package io.flow.play.util

import java.io.File
import java.nio.charset.Charset

import org.apache.commons.io.FileUtils

class UrlsSpec extends LibPlaySpec {

  "downloadToTmpFile" in {
    val tmp = File.createTempFile("test", "tmp")
    val contents = createTestId()
    FileUtils.writeStringToFile(tmp, contents, Charset.defaultCharset())
    val downloaded = Urls.downloadToTmpFile("file://" + tmp.toString)
    FileUtils.readFileToString(downloaded, Charset.defaultCharset()) must equal(contents)
  }

}
