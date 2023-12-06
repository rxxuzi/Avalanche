package net.http

import com.gargoylesoftware.htmlunit.html.{HtmlElement, HtmlPage}
import com.gargoylesoftware.htmlunit.{BrowserVersion, NicelyResynchronizingAjaxController, WebClient}

/**
 * 動的なWebページのコンテンツを取得するためのクローラー。
 * @param url クロール対象のURL
 * @author rxxuzi
 */
class DynamicCrawler(val url: String) {
  // HtmlUnitのWebClientを初期化
  private val webClient = new WebClient(BrowserVersion.FIREFOX)

  // JavaScriptとCSSの設定
  webClient.getOptions.setJavaScriptEnabled(true) // JavaScriptを有効にする
  webClient.getOptions.setCssEnabled(false) // CSSを無効にする
  webClient.getOptions.setTimeout(15000) // タイムアウトを15秒に設定

  // Ajaxリクエストのためのコントローラーを設定
  webClient.setAjaxController(new NicelyResynchronizingAjaxController())

  /**
   * 指定されたURLのページコンテンツを文字列として取得する。
   *
   * @param url 取得するページのURL
   * @return ページのHTMLコンテンツ。取得できない場合はNoneを返す。
   */
  def getPageContent(url: String): Option[String] = {
    try {
      val page: HtmlPage = webClient.getPage(url)
      Some(page.asXml())
    } catch {
      case _: Exception => None // 例外が発生した場合はNoneを返す
    }
  }

  def getPageContent: Option[String] = getPageContent(url)

  /**
   * プロキシサーバーの設定を行う。
   *
   * @param proxyHost プロキシサーバーのホスト名
   * @param proxyPort プロキシサーバーのポート番号
   */
  def setProxy(proxyHost: String, proxyPort: Int): Unit = {
    webClient.getOptions.getProxyConfig.setProxyHost(proxyHost)
    webClient.getOptions.getProxyConfig.setProxyPort(proxyPort)
  }

  /**
   * カスタムHTTPヘッダーを追加する。
   *
   * @param headerName  ヘッダー名
   * @param headerValue ヘッダー値
   */
  def setCustomHeader(headerName: String, headerValue: String): Unit = {
    webClient.addRequestHeader(headerName, headerValue)
  }

  /**
   * JavaScriptコードを実行する
   *
   * @param script 実行するJavaScriptコード
   * @return 実行結果。エラーが発生した場合はNoneを返す。
   */
  def executeJavaScript(script: String): Option[AnyRef] = {
    try {
      val page: HtmlPage = webClient.getPage(url)
      Some(page.executeJavaScript(script).getJavaScriptResult)
    } catch {
      case _: Exception => None
    }
  }

  /**
   * 指定されたタグ名を持つ要素をすべて見つける。
   *
   * @param tagName 検索するタグ名
   * @return 見つかった要素のリスト。エラーが発生した場合はNoneを返す。
   */
  def findElementsByTag(tagName: String): Option[List[HtmlElement]] = {
    try {
      val page: HtmlPage = webClient.getPage(url)
      Some(page.getByXPath(s"//$tagName").asInstanceOf[List[HtmlElement]])
    } catch {
      case _: Exception => None
    }
  }

  /**
   * 指定されたリンクテキストのリンクをクリックして、新しいページに移動する。
   *
   * @param linkText クリックするリンクのテキスト
   * @return 移動後のページ。エラーが発生した場合はNoneを返します。
   */
  def navigateToLink(linkText: String): Option[HtmlPage] = {
    try {
      val page: HtmlPage = webClient.getPage(url)
      val link = page.getFirstByXPath(s"//a[text()='$linkText']").asInstanceOf[HtmlElement]
      Some(link.click())
    } catch {
      case _: Exception => None
    }
  }

  /**
   * WebClientのリソースを解放。
   */
  def close(): Unit = {
    webClient.close()
  }

}

object DynamicCrawler {
  def apply(url: String): DynamicCrawler = new DynamicCrawler(url)
}

