package global

import java.io.File

object Config {
  val SAVE_EXT = ".txt"
  val APP_ICON = "/image/icon/Avalanche.png"
  val APP_NAME = "Avalanche"

  lazy val DATA_DIR : String = {
    val dir = System.getProperty("user.dir")
    val dataDir = dir + "/data/"
    mkdir(dataDir)
    dataDir
  }

  lazy val MEDIA_DIR : String = {
    val dir = System.getProperty("user.dir")
    val mediaDir = dir + "/output/media/"
    mkdir(mediaDir)
    mediaDir
  }

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
