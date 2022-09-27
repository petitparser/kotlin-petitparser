package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.cast
import org.petitparser.core.parser.action.pick
import org.petitparser.core.parser.combinator.seq

/** Returns a parser that succeeds at the end of input. */
fun endOfInput(message: String = "end of input expected") = object : Parser<Unit> {
  override fun parseOn(input: Input): Output<Unit> = if (input.position < input.buffer.length) {
    input.failure(message)
  } else {
    input.success(Unit)
  }
}

/** Returns a parser that succeeds only if the receiver consumes the complete input. */
fun <R> Parser<R>.end(message: String = "end of input expected"): Parser<R> =
  seq(this, endOfInput(message)).pick(0).cast()