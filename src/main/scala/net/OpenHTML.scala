package net

import util.security.{Security, UserAgent}

import java.net.{HttpURLConnection, URI, URL}
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

final class OpenHTML(url : String, async: Boolean) {

  // ExecutorServiceのインスタンスを保持
  private val executorService = Executors.newCachedThreadPool()

  // ExecutionContextをExecutorServiceを使って作成
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

  // ExecutorServiceをシャットダウンするメソッド
  def shutdown(): Unit = {
    executorService.shutdown()
  }

  private var htmlContent: String = ""

  def getHtml: String = fetchHtmlContent

  def getPageContent: Future[String] = {
    if (async) {
      Future {
        fetchHtmlContent
      }(ExecutionContext.global)
    } else Future.successful(fetchHtmlContent)
  }

  private def fetchHtmlContent: String = {
    if (Security.isUrlSafe(url) && Security.verifySSLCertificate(url)) {
      val uri = new URI(url)
      val connection = uri.toURL.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestProperty("User-Agent", UserAgent.getRandomUserAgent)

      val source = Source.fromInputStream(connection.getInputStream)
      try htmlContent = source.mkString
      finally source.close()

    } else {
      throw new IllegalArgumentException("URL is not safe or SSL verification failed")
    }
    htmlContent
  }

  def getTitle : String ={
    val document = new org.jsoup.nodes.Document(htmlContent)
    if(document.title == null) "example" else document.title
  }

  override def toString: String = htmlContent
}

object OpenHTML {
  def apply(url : String) = new OpenHTML(url, false)
  def apply(url : String, async: Boolean): OpenHTML = new OpenHTML(url ,async)
}
