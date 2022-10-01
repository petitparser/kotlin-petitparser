package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the result of the first succeeding of [parsers]. */
fun <R> or(vararg parsers: Parser<R>): Parser<R> = ChoiceParser(listOf(*parsers))

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix operator fun <R> Parser<R>.div(other: Parser<R>) = or(other)

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix fun <R> Parser<R>.or(other: Parser<R>): Parser<R> {
  val left = if (this is ChoiceParser<R>) parsers else listOf(this)
  val right = if (other is ChoiceParser<R>) other.parsers else listOf(other)
  return ChoiceParser(left + right)
}

private class ChoiceParser<R>(var parsers: List<Parser<R>>) : Parser<R> {
  override val children = parsers
  override fun parseOn(input: Input): Output<R> {
    var failures: MutableList<Output<R>>? = null
    for (parser in parsers) {
      when (val result = parser.parseOn(input)) {
        is Output.Success -> return result
        is Output.Failure -> {
          if (failures == null) failures = mutableListOf()
          failures.add(result)
        }
      }
    }
    return failures!!.last()
  }
}
