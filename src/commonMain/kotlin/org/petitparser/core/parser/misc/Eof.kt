package org.petitparser.core.parser.misc

import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.utils.Tuple2

/** Returns a parser that succeeds at the end of input. */
fun endOfInput(message: String = "end of input expected") = Parser { input ->
  if (input.position < input.buffer.length) {
    input.failure(message)
  } else {
    input.success(Unit)
  }
}

/** Returns a parser that succeeds only if the receiver consumes the complete input. */
fun <R> Parser<R>.end(message: String = "end of input expected"): Parser<R> =
  seq(this, endOfInput(message)).map(Tuple2<R, Unit>::first)