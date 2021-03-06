package com.hypertino.inflector.naming

class SnakeCaseBuilder(possibleLength: Option[Int] = None) extends IdentifierBuilder {
  private val sb = possibleLength.map {
    new StringBuilder(_)
  } getOrElse {
    new StringBuilder
  }

  override def divider(): Unit = {
    sb.append('_')
  }

  override def regular(c: Char): Unit = {
    sb.append(c.toLower)
  }

  override def toString() = {
    sb.toString()
  }
}
