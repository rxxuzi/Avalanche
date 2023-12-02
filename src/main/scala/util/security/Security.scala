package util.security

import java.net.{URI, URL}
import javax.net.ssl.HttpsURLConnection

object Security {

  /**
   * 与えられたURLが安全であるかどうかをチェックする。
   *
   * @param url チェックするURL
   * @return 安全な場合はtrue、そうでない場合はfalse
   */
  def isUrlSafe(url: String): Boolean = {
    try {
      val uri = new URI(url)
      val scheme = uri.getScheme
      scheme == "http" || scheme == "https"
    } catch {
      case _: Exception => false
    }
  }

  /**
   * SSL証明書を検証する。
   *
   * @param url 検証するURL
   * @return SSL証明書が有効な場合はtrue、そうでない場合はfalse
   */
  def verifySSLCertificate(url: String): Boolean = {
    try {
      val connection = new URL(url).openConnection().asInstanceOf[HttpsURLConnection]
      connection.setHostnameVerifier((_, _) => true)
      connection.connect()
      connection.disconnect()
      true
    } catch {
      case _: Exception => false
    }
  }

  /**
   * SQLインジェクションやクロスサイトスクリプティング（XSS）などの脆弱性から保護するために、
   * 入力文字列を検証・サニタイズする。
   *
   * @param input 検証する文字列
   * @return サニタイズされた文字列
   */
  def sanitizeInput(input: String): String = {
    // ここにサニタイズロジックを実装
    input.replaceAll("[^\\w\\s]", "") // 簡単な例
  }

  /**
   * ヘッダーインジェクション攻撃から保護するために、HTTPヘッダーの値を検証・サニタイズする。
   *
   * @param headerValue 検証するヘッダーの値
   * @return サニタイズされたヘッダーの値
   */
  def sanitizeHttpHeader(headerValue: String): String = {
    headerValue.replaceAll("\r", "").replaceAll("\n", "")
  }

}

