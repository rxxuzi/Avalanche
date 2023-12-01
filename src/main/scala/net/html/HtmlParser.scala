package net.html
import net.html.HtmlParser.extractText
import org.jsoup.Jsoup

import scala.util.{Failure, Success, Try}

class HtmlParser(content: String){
  private val document = Jsoup.parse(content)

  def getTitle: String = document.title()
  def getSource : String = document.baseUri()

  def getText: String = {
    extractText(content) match {
      case Success(value) => value
      case Failure(e) => s"Error parsing HTML: ${e.getMessage}"
    }
  }
}
object HtmlParser {
  def apply(content: String): HtmlParser = new HtmlParser(content)

  /**
   * HTMLコンテンツから全てのテキストを抽出する。
   *
   * @param html HTML形式の文字列
   * @return 抽出されたテキスト
   */
  private def extractText(html: String): Try[String] = Try {
    Jsoup.parse(html).text()
  }
}
