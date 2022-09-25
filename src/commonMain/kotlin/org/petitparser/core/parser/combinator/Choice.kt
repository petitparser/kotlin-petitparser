package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success
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
  override fun parseOn(context: Context): Result<R> {
    var failures: MutableList<Result<R>>? = null
    for (parser in parsers) {
      val result = parser.parseOn(context)
      if (result is Success<R>) {
        return result
      }
      if (failures == null) {
        failures = mutableListOf()
      }
      failures.add(result)
    }
    return failures!!.last()
  }
}
