package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that discards the result of the receiver and returns the sub-string this parser consumes. */
fun Parser<*>.flatten(message: String? = null): Parser<String> =
  FlattenParser(this, message)

/** A parser that discards the result of the delegate and returns the consumed sub-string. */
class FlattenParser(
  delegate: Parser<*>,
  val message: String? = null,
) : DelegateParser<Any?, String>(delegate) {
  override fun parseOn(input: Input): Output<String> {
    return if (message != null) {
      val position = delegate.fastParseOn(input.buffer, input.position)
      if (position < 0) {
        input.failure(message)
      } else {
        input.success(input.buffer.substring(input.position, position), position)
      }
    } else {
      when (val result = delegate.parseOn(input)) {
        is Output.Success -> result.success(input.buffer.substring(input.position, result.position))
        is Output.Failure -> result
      }
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun toString(): String =
    if (message == null) super.toString() else "${super.toString()}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is FlattenParser && message == other.message

  override fun copy(): FlattenParser = FlattenParser(delegate, message)
}