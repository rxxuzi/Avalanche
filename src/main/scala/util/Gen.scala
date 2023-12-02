package util

import global.Config.SAVE_DIR
import util.Gen.sanitizeFilename

import java.io.{BufferedWriter, File, FileWriter, IOException}

class Gen(private val data: String) {
  def saveToTxt(filename: String) : Unit = {
    val file = new File(SAVE_DIR + sanitizeFilename(filename) + ".txt")
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(data)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    } finally writer.close()
    println(s"Saved to ${file.getPath}")
  }
}

object Gen{
  def apply(data: String): Gen = new Gen(data)

  def sanitizeFilename(filename: String): String = {
    // ファイル名に使用できない文字のリスト
    val invalidChars = "[\\\\/:*?\"<>|]".r
    invalidChars.replaceAllIn(filename, "_")
  }
}