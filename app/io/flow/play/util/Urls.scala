package io.flow.play.util

import java.net.URL
import java.io.File
import org.apache.commons.io.FileUtils

object Urls {

  def downloadToFile(url: String, prefix: String, suffix: String): File = {
    val tmp = File.createTempFile(prefix, suffix)
    FileUtils.copyURLToFile(new URL(url), tmp)
    tmp
  }

}
