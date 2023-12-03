package util

import scala.collection.mutable
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._

/**
 * RateLimiterクラスは、指定された時間内に特定の数のリクエストのみを許可する
 *
 * @param maxRequests 最大リクエスト数
 * @param duration リクエストの間隔
 * @param executionContext 非同期実行のためのExecutionContext
 */
class RateLimiter(maxRequests: Int, duration: FiniteDuration)(implicit executionContext: ExecutionContext) {

  private val requestTimestamps: mutable.Queue[Long] = mutable.Queue.empty[Long]

  /**
   * 指定されたアクションを実行しますが、レートリミットを超えないように待機する可能性がある。
   *
   * @param action 実行するアクション
   * @tparam T アクションの結果の型
   * @return アクションの結果を含むFuture
   */
  def execute[T](action: => Future[T]): Future[T] = synchronized {
    val now = System.currentTimeMillis()

    // 古いタイムスタンプを削除
    while (requestTimestamps.headOption.exists(_ < now - duration.toMillis)) {
      requestTimestamps.dequeue()
    }

    // リクエストのレートが制限を超えている場合、待機
    if (requestTimestamps.size >= maxRequests) {
      val waitTime = duration.toMillis - (now - requestTimestamps.head)
      Thread.sleep(waitTime)
    }

    // タイムスタンプを追加し、アクションを実行
    requestTimestamps.enqueue(now)
    action
  }
}
