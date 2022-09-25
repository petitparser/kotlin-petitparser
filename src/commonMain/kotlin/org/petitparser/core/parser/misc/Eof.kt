package org.petitparser.core.parser.misc

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.seq

/** Returns a parser that succeeds at the end of input. */
fun endOfInput(message: String = "end of input expected") = object : Parser<Unit> {
  override fun parseOn(context: Context): Result<Unit> =
    if (context.position < context.buffer.length) {
      context.failure(message)
    } else {
      context.success(Unit)
    }
}

/** Returns a parser that succeeds only if the receiver consumes the complete input. */
fun <R> Parser<R>.end(message: String = "end of input expected") =
  seq(this, if (message != null) endOfInput(message) else endOfInput()).map { it[1] as R }