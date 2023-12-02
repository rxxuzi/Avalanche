package global

object Config {
  val SAVE_DIR = "./output/"
  val SAVE_EXT = ".txt"
  val APP_ICON = "/image/icon/Avalanche.png"
  lazy val LOG_DIR : String= {
    val dir = System.getProperty("user.dir")
    val logDir = dir + "/log/"
    if (!new java.io.File(logDir).exists()){
      new java.io.File(logDir).mkdir()
    }
    logDir
  }
}
