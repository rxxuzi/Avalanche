package util

import scala.collection.mutable
import scala.concurrent.duration._

/**
 * CacheManagerは、ウェブページのコンテンツをキャッシュするクラス。
 *
 * @param expiryDuration キャッシュの有効期限
 */
class CacheManager(expiryDuration: FiniteDuration) {

  private case class CacheEntry(content: String, timestamp: Long)

  private val cache: mutable.Map[String, CacheEntry] = mutable.Map()

  /**
   * キャッシュにコンテンツを保存します。
   *
   * @param url キャッシュするコンテンツのURL
   * @param content キャッシュするコンテンツ
   */
  def put(url: String, content: String): Unit = {
    val entry = CacheEntry(content, System.currentTimeMillis())
    cache.put(url, entry)
  }

  /**
   * キャッシュからコンテンツを取得する。
   * キャッシュが存在しないか、期限切れの場合はNoneを返す。
   *
   * @param url 取得するコンテンツのURL
   * @return キャッシュされたコンテンツ（存在する場合）
   */
  def get(url: String): Option[String] = {
    cache.get(url).filter { entry =>
      (System.currentTimeMillis() - entry.timestamp) < expiryDuration.toMillis
    }.map(_.content)
  }
}

