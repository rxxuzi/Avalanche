package io

import java.io.{File, IOException, PrintWriter}

abstract class Gen {
  val data : String = ""
  val dir = "./"
  val ext = ".txt"
  var path : String = ""

  def save(filename: String): Unit = {
    val filepath = sanitizeFilename(filename)
    write(filepath, data)
  }

  def getPath : String = new File(path).getAbsolutePath

  def write(path : String, data: String): Unit = {
    this.path = dir + path + ext
    val file = new File(dir + sanitizeFilename(filename = path) + ext)
    val pw = new PrintWriter(file)
    try pw.write(data)
    catch {
      case e: IOException => throw new RuntimeException(e)
    }
    finally pw.close()
  }

  private def sanitizeFilename(filename: String): String = {
    val invalidChars = "[\\\\/:*?\"<>|]".r
    invalidChars.replaceAllIn(filename, "_")
  }
}
