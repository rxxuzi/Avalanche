package net

import java.net.URI
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
    print("Shutdown")
    executorService.shutdown()
  }

  private var htmlContent: String = ""

  def getPageContent: Future[String] = {
    if (async) {
      Future {
        fetchHtmlContent
      }(ExecutionContext.global)
    } else Future.successful(fetchHtmlContent)
  }

  private def fetchHtmlContent: String = {
    val uri = new URI(url)
    val source = Source.fromURL(uri.toURL)
    try htmlContent = source.mkString
    finally source.close()
    htmlContent
  }

  def getTitle : String ={
    val document = new org.jsoup.nodes.Document(htmlContent)
    if(document.title == null) "example" else document.title
  }
}

object OpenHTML {
  def apply(url : String) = new OpenHTML(url, false)
  def apply(url : String, async: Boolean): OpenHTML = new OpenHTML(url ,async)
}
