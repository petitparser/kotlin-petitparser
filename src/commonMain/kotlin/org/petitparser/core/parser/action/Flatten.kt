package org.petitparser.core.parser.action

import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that discards the result of the receiver and returns the sub-string this parser consumes. */
fun <R> Parser<R>.flatten() = Parser { input ->
  when (val result = this@flatten.parseOn(input)) {
    is Output.Success -> result.success(input.buffer.substring(input.position, result.position))
    is Output.Failure -> result
  }
}