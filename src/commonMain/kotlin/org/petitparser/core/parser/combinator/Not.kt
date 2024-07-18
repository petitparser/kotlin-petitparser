package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.consumer.any

/** Returns a parser that succeeds with the [Output.Failure] whenever the receiver fails, but never consumes input. */
fun <R> Parser<R>.not(message: String = "no success expected") =
  Parser { input ->
    when (val result = this@not.parseOn(input)) {
      is Output.Success -> input.failure(message)
      is Output.Failure -> input.success(result)
    }
  }

/** Returns a parser that consumes a character, if it does not match the receiver. */
fun <R> Parser<R>.neg(message: String = "input not expected") =
  any().skip(not(message))
