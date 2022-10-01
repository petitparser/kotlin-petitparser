package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that succeeds with the [Output.Failure] whenever the receiver fails, but never consumes input. */
fun <R> Parser<R>.not(message: String = "no success expected") =
  object : Parser<Output.Failure<R>> {
    override fun parseOn(input: Input) = when (val result = this@not.parseOn(input)) {
      is Output.Success -> input.failure(message)
      is Output.Failure -> input.success(result)
    }
  }