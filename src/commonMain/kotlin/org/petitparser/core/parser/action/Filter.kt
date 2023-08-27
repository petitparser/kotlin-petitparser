package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.parser.Parser

/**
 * Returns a parser that evaluates the [predicate] with the successful parse result. If the
 * predicate returns `true` the parser proceeds with the parse result, otherwise a parse failure is
 * created using [failureFactory]. */
fun <T> Parser<T>.filter(
  predicate: (T) -> Boolean,
  failureFactory: (Input, Output.Success<T>) -> Output.Failure = { input, result ->
    input.failure("unexpected '${result.value}'")
  },
) = object : Parser<T> {
  override fun parseOn(input: Input) = when (val result = this@filter.parseOn(input)) {
    is Output.Success -> when (predicate(result.value)) {
      true -> result
      false -> failureFactory(input, result)
    }
    is Output.Failure -> result
  }
}

