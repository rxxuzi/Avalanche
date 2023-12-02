package io

import global.Config

class Text(private val value: String) extends Gen {
  override val data: String = value
  override val dir : String = Config.SAVE_DIR
  override val ext : String = Config.SAVE_EXT
}

object Text{
  def apply(data: String): Text = new Text(value = data)
}