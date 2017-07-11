package io.flow.play.util

import sys.process._
import java.net.URL
import java.io.File

object Urls {

  def downloadToFile(url: String, file: File): File = {
    new URL(url) #> file !!

    file
  }

  def downloadToTmpFile(url: String, prefix: String, suffix: String): File = {
    downloadToFile(url, File.createTempFile(prefix, suffix))
  }

}
