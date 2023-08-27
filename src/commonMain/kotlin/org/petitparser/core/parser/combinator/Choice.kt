package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.utils.FailureJoiner
import org.petitparser.core.parser.utils.selectLast

/** Returns a parser that accepts the result of the first succeeding of [parsers]. */
fun <R> or(
  vararg parsers: Parser<R>,
  failureJoiner: FailureJoiner = ::selectLast,
): Parser<R> = ChoiceParser(listOf(*parsers), failureJoiner)

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix operator fun <R> Parser<R>.div(other: Parser<R>) = or(other)

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix fun <R> Parser<R>.or(
  other: Parser<R>,
): Parser<R> {
  val left = if (this is ChoiceParser<R>) parsers else listOf(this)
  val right = if (other is ChoiceParser<R>) other.parsers else listOf(other)
  return ChoiceParser(left + right)
}

private class ChoiceParser<R>(
  val parsers: List<Parser<R>>,
  val failureJoiner: FailureJoiner = ::selectLast,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    var failure: Output.Failure? = null
    for (parser in parsers) {
      when (val result = parser.parseOn(input)) {
        is Output.Success -> return result
        is Output.Failure -> {
          failure = if (failure == null) result
          else failureJoiner(failure, result)
        }
      }
    }
    return failure!!
  }
}
