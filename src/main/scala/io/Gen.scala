package io

import java.io.{File, IOException, PrintWriter}

abstract class Gen {
  val data : String = ""
  val dir = "./"
  val ext = ".txt"

  def save(filename: String): Unit = {
    val path = sanitizeFilename(filename)
    val file = new File(dir + path + ext)
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
