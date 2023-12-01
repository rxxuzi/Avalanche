package util

import global.Config.SAVE_DIR

import java.io.{BufferedWriter, File, FileWriter, IOException}

class Gen(private val data: String) {
  def saveToTxt(filename: String) : Unit = {
    val file = new File(SAVE_DIR + filename + ".txt")
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
}