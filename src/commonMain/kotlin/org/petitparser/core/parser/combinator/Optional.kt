package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns new parser that accepts the receiver, otherwise returns `null`. */
fun <R> Parser<R>.optional(): OptionalParser<R?> = OptionalParser(this, null)

/** Returns new parser that accepts the receiver, otherwise returns [otherwise]. */
fun <R> Parser<R>.optional(otherwise: R): OptionalParser<R> = OptionalParser(this, otherwise)

/** Returns new parser that accepts the receiver, otherwise returns [otherwise]. */
fun <R> Parser<R>.optionalWith(otherwise: R): OptionalParser<R> = optional(otherwise)

/**
 * A parser that optionally parses its [delegate], or answers [otherwise].
 */
class OptionalParser<R>(
  delegate: Parser<R>,
  val otherwise: R,
) : DelegateParser<R, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> result
      is Output.Failure -> input.success(otherwise)
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val result = delegate.fastParseOn(buffer, position)
    return if (result < 0) position else result
  }

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is OptionalParser<*> &&
      otherwise == other.otherwise

  override fun copy(): OptionalParser<R> = OptionalParser(delegate, otherwise)
}