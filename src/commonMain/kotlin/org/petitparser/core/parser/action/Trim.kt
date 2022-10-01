package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.consumer.whitespace

/**
 * Returns a parser that consumes input before and after the receiver, discards the excess input
 * and only returns the result of the receiver.
 */
fun <R> Parser<R>.trim(left: Parser<Any> = whitespace(), right: Parser<Any> = left) =
  object : Parser<R> {
    override fun parseOn(input: Input): Output<R> {
      val before = consumeAll(left, input)
      val result = this@trim.parseOn(before)
      if (result is Output.Failure) return result
      val after = consumeAll(right, result)
      return after.success(result.value)
    }
  }

private fun <R> consumeAll(parser: Parser<R>, input: Input): Input {
  var current = input
  while (true) {
    when (val result = parser.parseOn(current)) {
      is Output.Success -> current = result
      is Output.Failure -> return current
    }
  }
}