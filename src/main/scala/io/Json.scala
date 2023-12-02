package io

import global.Config

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable
import scala.util.hashing.MurmurHash3

class Json extends Gen {
  private val dataMap: mutable.Map[String, Map[String, String]] = mutable.LinkedHashMap()
  private var keyCounter = 0
  override val ext: String = ".json"
  override val dir: String = Config.LOG_DIR

  private def convertPathForJson(path: String): String = {
    path.replace("\\", "/")
  }

  def addData(url: String, originalPath: String): Unit = {
    val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
    val convertedPath = convertPathForJson(originalPath)
    val hash = MurmurHash3.stringHash(url + convertedPath + time).toString
    val data = Map("time" -> time, "url" -> url, "path" -> convertedPath, "hash" -> hash)
    dataMap(s"${"%04d".format(keyCounter)}") = data
    keyCounter += 1
  }


  override def save(filename: String): Unit = {
    val json = dataMap.map {
      case (key, valueMap) =>
        val keyValuePairs = valueMap.map { case (k, v) => s"""    "$k": "$v"""" }.mkString(",\n")
        s"""  "$key": {\n$keyValuePairs\n  }"""
    }.mkString("{\n", ",\n", "\n}")
    if(keyCounter > 0){
      write(filename, json)
    }
  }
}
