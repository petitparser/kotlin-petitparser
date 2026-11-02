package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.SkipParser
import org.petitparser.core.parser.combinator.skip

/** Returns a parser that succeeds at the end of input. */
fun endOfInput(message: String = "end of input expected"): EndOfInputParser =
  EndOfInputParser(message)

/** Returns a parser that succeeds only if the receiver consumes the complete input. */
fun <R> Parser<R>.end(message: String = "end of input expected"): SkipParser<R> =
  skip(after = endOfInput(message))

/** A parser that succeeds at the end of input. */
class EndOfInputParser(
  val message: String = "end of input expected",
) : Parser<Unit> {
  override fun parseOn(input: Input): Output<Unit> =
    if (input.position < input.buffer.length) {
      input.failure(message)
    } else {
      input.success(Unit)
    }

  override fun fastParseOn(buffer: String, position: Int): Int =
    if (position < buffer.length) -1 else position

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun copy(): EndOfInputParser = EndOfInputParser(message)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is EndOfInputParser && message == other.message
}
