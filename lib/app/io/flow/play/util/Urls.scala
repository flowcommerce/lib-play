package io.flow.play.util

import java.net.URL
import java.io.File
import org.apache.commons.io.FileUtils

object Urls {

  def downloadToFile(url: String, file: File): File = {
    downloadToFile(new URL(url), file)
  }

  def downloadToFile(url: URL, file: File): File = {
    FileUtils.copyURLToFile(url, file)
    file
  }

  def downloadToTmpFile(url: String, prefix: String = "download", suffix: String = "tmp"): File = {
    val finalSuffix = if (suffix.startsWith(".")) { suffix }
    else { "." + suffix }
    downloadToFile(url, File.createTempFile(prefix, finalSuffix))
  }

}
