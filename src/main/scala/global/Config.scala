package global

import java.io.File

object Config {
  val SAVE_EXT = ".txt"
  val APP_ICON = "/image/icon/Avalanche.png"
  val APP_NAME = "Avalanche"

  lazy val SAVE_DIR :String = {
    val dir = System.getProperty("user.dir")
    val saveDir = dir + "/output/"
    mkdir(saveDir)
    saveDir
  }

  lazy val LOG_DIR : String= {
    val dir = System.getProperty("user.dir")
    val logDir = dir + "/log/"
    mkdir(logDir)
    logDir
  }

  private def mkdir(path : String) : Unit = {
    val file = new File(path)
    if (!file.exists) {
      file.mkdir()
    }
  }
}
