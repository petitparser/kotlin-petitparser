package org.petitparser.core.context

/** Input interface of the parser function. */
interface Input {
  /** The input buffer being read. */
  val buffer: String

  /** The position in the input buffer being read. */
  val position: Int

  /** The line of the current position in the buffer. */
  val line: Int get() = lineAndColumn(buffer, position).line

  /** The column of the current position in the buffer. */
  val column: Int get() = lineAndColumn(buffer, position).column

  /** Returns a string representation of the current line and column. */
  fun toPositionString(): String = "$line:$column"

  /** Actual implementation of the [Input] interface. */
  data class Impl(override val buffer: String, override val position: Int) : Input
}

/** Constructs an [Input] instance. */
fun Input(buffer: String, position: Int = 0): Input = Input.Impl(buffer, position)
