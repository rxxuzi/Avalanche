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

//  // ExecutorServiceのインスタンスを保持
//  private val executorService = Executors.newCachedThreadPool()
//
//  // ExecutionContextをExecutorServiceを使って作成
//  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
//
//  // ExecutorServiceをシャットダウンするメソッド
////  private def shutdown(): Unit = {
////    executorService.shutdown()
////  }
  // HTMLコンテンツを格納する変数
  private var htmlContent: String = fetchHtmlContent
//
//  // URLからHTMLコンテンツを非同期で取得
//  private val futureHtmlContent: Future[String] = crawl
//
//  // Futureの結果が利用可能になったときに実行されるコールバック
//  futureHtmlContent.onComplete {
//    case Success(content) =>
//      success  = true
//      htmlContent = content
//    case Failure(ex) =>
//      success = false
//      println(s"エラーが発生しました: ${ex.getMessage}")
//  }
//
//  def crawl: Future[String] = {
//    if (async) {
//      Future {
//        fetchHtmlContent
//      }(ExecutionContext.global)
//    } else Future.successful(fetchHtmlContent)
//  }

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
