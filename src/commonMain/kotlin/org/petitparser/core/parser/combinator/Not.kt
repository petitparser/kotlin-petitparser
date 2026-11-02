package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.consumer.any

/** Returns a parser that succeeds with the [Output.Failure] whenever the receiver fails, but never consumes input. */
fun <R> Parser<R>.not(message: String = "no success expected"): NotParser<R> =
  NotParser(this, message)

/** Returns a parser that consumes a character, if it does not match the receiver. */
fun <R> Parser<R>.neg(message: String = "input not expected"): SkipParser<Char> =
  any().skip(before = not(message))

/**
 * The not-predicate, a parser that succeeds whenever its [delegate] does not,
 * but consumes no input.
 */
class NotParser<R>(
  delegate: Parser<R>,
  val message: String = "no success expected",
) : DelegateParser<R, Output.Failure>(delegate) {
  override fun parseOn(input: Input): Output<Output.Failure> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> input.failure(message)
      is Output.Failure -> input.success(result)
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val result = delegate.fastParseOn(buffer, position)
    return if (result < 0) position else -1
  }

  override fun toString(): String = "${super.toString()}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is NotParser<*> &&
      message == other.message

  override fun copy(): NotParser<R> = NotParser(delegate, message)
}

