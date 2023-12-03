package net.download

import global.Config

import java.io.{BufferedInputStream, FileOutputStream}
import java.net.URI
import scala.util.{Failure, Success, Try}

class MediaDownloader {
  private var mediaCount = 0
  private var c = 0

  def download(fileUrl: String, savePath: String): Unit = {
    Try {
      val url = new URI(fileUrl).toURL
      val connection = url.openConnection()
      val in = new BufferedInputStream(connection.getInputStream)
      val out = new FileOutputStream(savePath)
      val buffer = new Array[Byte](1024)
      var bytesRead = 0
      while ({ bytesRead = in.read(buffer); bytesRead != -1 }) {
        out.write(buffer, 0, bytesRead)
      }
      in.close()
      out.close()
    } match {
      case Success(_) =>
        mediaCount += 1
      case Failure(e) => println(s"Error downloading $fileUrl: ${e.getMessage}")
    }
  }

  def downloads(media: Seq[(String, String)]): Unit = {
    c = media.length
    media.foreach { case (src, alt) =>
      val extension = src.split('.').last
      val fileName = if (alt == "null") "default" else alt.replaceAll("\\s+", "_")
      val savePath = s"${Config.MEDIA_DIR}$fileName.$extension"
      Try(new URI(src).toURL) match {
        case Success(_) => download(src, savePath)
        case Failure(e) =>
          println(s"Invalid URL: $src - ${e.getMessage}")
      }
    }
  }
}


