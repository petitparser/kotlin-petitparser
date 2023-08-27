package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure

/** Parses the provided [input]. */
fun <R> Parser<R>.parse(input: String, start: Int = 0): Output<R> =
  parseOn(Input.Impl(input, start))

/** Tests if the [input] can be successfully parsed. */
fun <R> Parser<R>.accept(input: String, start: Int = 0) = when (parse(input, start)) {
  is Output.Success -> true
  is Output.Failure -> false
}

/** Returns a lazy sequence over all successful parse results over the provided [input]. */
fun <R> Parser<R>.matches(
  input: String,
  overlapping: Boolean = false,
  start: Int = 0,
): Sequence<R> = sequence {
  var context: Input = Input.Impl(input, start)
  while (context.position < input.length) {
    context = when (val result = parseOn(context)) {
      is Output.Success -> {
        yield(result.value)
        if (overlapping || context.position == result.position) {
          result.failure("", context.position + 1)
        } else {
          result
        }
      }
      is Output.Failure -> result.failure("", context.position + 1)
    }
  }
}