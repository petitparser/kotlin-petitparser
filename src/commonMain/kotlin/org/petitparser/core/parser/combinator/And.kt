package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that succeeds whenever the receiver does, but never consumes input. */
fun <R> Parser<R>.and(): AndParser<R> = AndParser(this)

/**
 * The and-predicate, a parser that succeeds whenever its [delegate] does, but
 * does not consume the input stream.
 */
class AndParser<R>(delegate: Parser<R>) : DelegateParser<R, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> input.success(result.value)
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val result = delegate.fastParseOn(buffer, position)
    return if (result < 0) -1 else position
  }

  override fun copy(): AndParser<R> = AndParser(delegate)
}