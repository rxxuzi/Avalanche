package global

import java.io.File

object Config {
  val SAVE_EXT = ".txt"
  val APP_ICON = "/image/icon/Avalanche.png"
  val APP_NAME = "Avalanche"

  lazy val DATA_DIR : String = mkdir("data/")

  lazy val MEDIA_DIR : String = mkdir("/output/media/")
  lazy val HTML_DIR : String = mkdir("/output/html/")
  lazy val TEXT_DIR : String = mkdir("/output/text/")
  lazy val SAVE_DIR :String = mkdir("/output/")
  lazy val LOG_DIR : String= mkdir("/log/")

  private def mkdir(path : String) : String = {
    val dir = System.getProperty("user.dir")
    val file = new File(dir + path)
    if (!file.exists) {
      file.mkdir()
    }
    file.getPath + "/"
  }
}
