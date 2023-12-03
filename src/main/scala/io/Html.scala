package io

import global.Config
import org.jsoup.nodes.Entities
import org.jsoup.Jsoup
import org.jsoup.nodes.Document._
import org.jsoup.nodes.Entities.EscapeMode

class Html(val value : String) extends Gen {
  override val data: String = formatHTML(value)
  override val ext: String = ".html"
  override val dir: String = Config.SAVE_DIR + "html/"

  override def save(filename: String): Unit = {
    val path = dir + filename + ext
    write(path, data)
  }

  private def formatHTML(html: String): String = {
    val doc = Jsoup.parse(html)
    doc.outputSettings(new OutputSettings().prettyPrint(true).escapeMode(EscapeMode.xhtml))
    doc.toString
  }
}

object Html {
  def apply(html: String): Html = new Html(html)
}
