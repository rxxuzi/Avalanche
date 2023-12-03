package net

import net.html.HtmlParser
import net.http.Crawler
import util.security.Security

import java.net.URI
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

final class OpenHTML(url : String, async: Boolean) {
  if (url == null) throw new IllegalArgumentException("URL cannot be null")
  private var htmlContent: String = fetchHtmlContent
  private def fetchHtmlContent: String = {
    if (Security.isUrlSafe(url) && Security.verifySSLCertificate(url)) {
      val uri = new URI(url)
      try htmlContent = Crawler.crawl(uri.toURL)
    } else {
      throw new IllegalArgumentException("URL is not safe or SSL verification failed")
    }
    htmlContent
  }

  def getTitle : String ={
    val document = new org.jsoup.nodes.Document(htmlContent)
    if(document.title == null) "example" else document.title
  }

  def getHtml : String = htmlContent

  // HtmlParserオブジェクトを生成するメソッド
  def toHTMLParser : HtmlParser = HtmlParser(htmlContent)

  override def toString: String = htmlContent
}

object OpenHTML {
  def apply(url : String) = new OpenHTML(url, false)
  def apply(url : String, async: Boolean): OpenHTML = new OpenHTML(url ,async)
}
