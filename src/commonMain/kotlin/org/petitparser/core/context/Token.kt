package org.petitparser.core.context

import org.petitparser.core.parser.action.token
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.matches

/** A token represents a parsed part of the input stream. */
data class Token<R>(
  /** The parsed value of the token. */
  val value: R,
  /** The parsed buffer of the token. */
  val buffer: String,
  /** The start position of the token in the buffer. */
  val start: Int,
  /** The stop position of the token in the buffer. */
  val stop: Int,
) {
  /** The consumed input of the token. */
  val input = buffer.substring(start, stop)

  /** The length of the token. */
  val length = stop - start

  /** The line of the token. */
  val line: Int get() = lineAndColumn.line

  /** The column of the token */
  val column: Int get() = lineAndColumn.column

  /** The line and column of the token. */
  private val lineAndColumn by lazy { lineAndColumn(buffer, start) }
}

private data class LineAndColumn(val line: Int, val column: Int)

private fun lineAndColumn(buffer: String, position: Int): LineAndColumn {
  var line = 1
  var offset = 0
  for (token in newline().token().matches(buffer)) {
    if (position < token.stop) {
      return LineAndColumn(line, position - offset + 1)
    }
    line++
    offset = token.stop
  }
  return LineAndColumn(line, position - offset + 1)
}