package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that succeeds whenever the receiver does, but never consumes input. */
fun <R> Parser<R>.and() = Parser { input ->
  when (val result = this@and.parseOn(input)) {
    is Output.Success -> input.success(result.value)
    is Output.Failure -> result
  }
}