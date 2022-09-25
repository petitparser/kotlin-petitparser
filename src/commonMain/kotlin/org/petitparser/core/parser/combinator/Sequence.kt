package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Failure
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts a list of [parsers]. */
fun <R> seq(vararg parsers: Parser<R>): Parser<List<R>> = SequenceParser(listOf(*parsers))

/** Returns the sequence of this parser followed by [other]. */
infix operator fun <R> Parser<R>.plus(other: Parser<R>) = seq(other)

/** Returns the sequence of this parser followed by [other]. */
infix fun <R> Parser<out R>.seq(other: Parser<out R>): Parser<List<R>> {
  val left = if (this is SequenceParser<*>) parsers as List<Parser<R>> else listOf(this)
  val right = if (other is SequenceParser<*>) other.parsers as List<Parser<R>> else listOf(other)
  return SequenceParser(left + right)
}

private class SequenceParser<R>(var parsers: List<Parser<R>>) : Parser<List<R>> {
  override fun parseOn(context: Context): Result<List<R>> {
    var current = context
    val elements = mutableListOf<R>()
    for (parser in parsers) {
      val result = parser.parseOn(current)
      if (result is Failure) {
        return result.failure(result.message)
      }
      elements.add(result.value)
      current = result
    }
    return current.success(elements)
  }
}

