package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts a list of [parsers]. */
fun <R> seq(vararg parsers: Parser<R>): Parser<List<R>> = SequenceParser(listOf(*parsers))

/** Returns the sequence of this parser followed by [other]. */
infix operator fun Parser<*>.plus(other: Parser<Any?>) = seq(other)

/** Returns the sequence of this parser followed by [other]. */
infix fun Parser<Any?>.seq(other: Parser<Any?>): Parser<List<Any?>> {
  val left = if (this is SequenceParser<*>) parsers else listOf(this)
  val right = if (other is SequenceParser<*>) other.parsers else listOf(other)
  return SequenceParser(left + right)
}

private class SequenceParser<R>(val parsers: List<Parser<R>>) : Parser<List<R>> {
  override fun parseOn(input: Input): Output<List<R>> {
    var current = input
    val elements = mutableListOf<R>()
    for (parser in parsers) {
      when (val result = parser.parseOn(current)) {
        is Output.Success -> {
          elements.add(result.value)
          current = result
        }
        is Output.Failure -> return result
      }
    }
    return current.success(elements)
  }
}

