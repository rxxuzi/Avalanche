package net.html
import net.html.HtmlParser.extractText
import org.jsoup.Jsoup
import scala.util.{Failure, Success, Try}
import scala.jdk.CollectionConverters._

class HtmlParser(content: String){
  private val document = Jsoup.parse(content)

  def getTitle: String = document.title()
  def getSource : String = document.baseUri()

  def getText: String = getText(false)

  /**
   * HTMLコンテンツからテキストを抽出する。
   *
   * @param includeNewLines 改行を含むかどうかを指定するブール値。
   *                        trueの場合、pタグやbrタグなどから改行を含むテキストを返す。
   *                        falseの場合、改行なしのテキストを返す。
   * @return 抽出されたテキスト
   */
  def getText(includeNewLines: Boolean): String = {
    if (includeNewLines) {
      document.select("br").append("\\n")
      document.select("p").prepend("\\n\\n")
      document.text().replaceAll("\\\\n", "\n").trim
    } else {
      extractText(content) match {
        case Success(value) => value
        case Failure(e) => s"Error parsing HTML: ${e.getMessage}"
      }
    }
  }

  /**
   * HTMLコンテンツからヘッダー要素を抽出する。
   *
   * @return 各ヘッダー要素（h1, h2, h3, etc.）のテキストを含むマップ
   */
  def getHeaders: Map[String, Seq[String]] = {
    val headerTags = Seq("h1", "h2", "h3", "h4", "h5", "h6")
    headerTags.map { tag =>
      tag -> document.select(tag).eachText().asScala.toSeq
    }.toMap
  }

  // リンクを抽出するメソッド
  def getLinks: Seq[String] = document.select("a[href]").eachAttr("abs:href").asScala.toSeq

  // 画像を抽出するメソッド
  def getImages: Seq[(String, String)] = {
    document.select("img[src]").asScala.toSeq.map { img =>
      val src = img.absUrl("src")
      val alt = Option(img.attr("alt")).getOrElse("null")
      (src, alt)
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
