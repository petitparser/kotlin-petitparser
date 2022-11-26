package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that returns a [Token]. */
fun <R> Parser<R>.token() = object : Parser<Token<R>> {
  override fun parseOn(input: Input) = when (val result = this@token.parseOn(input)) {
    is Output.Success -> result.success(
      Token(
        result.value,
        input.buffer,
        input.position,
        result.position,
      )
    )
    is Output.Failure -> result.failure(result.message)
  }
}

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
}