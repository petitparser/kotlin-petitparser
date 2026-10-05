package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.utils.SequentialParser

/** Returns a parser that accepts a list of [parsers]. */
fun <R> seqOf(vararg parsers: Parser<R>): SequenceParser<R> = SequenceParser(parsers.toList())

/** Returns a parser that accepts a list of [parsers]. */
fun <R> seqOf(parsers: Iterable<Parser<R>>): SequenceParser<R> = SequenceParser(parsers)

/** Combines this iterable of parsers into a single [SequenceParser]. */
fun <R> Iterable<Parser<R>>.toSequenceParser(): SequenceParser<R> = SequenceParser(this)

/** Returns the sequence of this parser followed by [other]. */
infix fun Parser<*>.seq(other: Parser<*>): SequenceParser<Any?> {
  val left = if (this is SequenceParser<*>) children else listOf(this)
  val right = if (other is SequenceParser<*>) other.children else listOf(other)
  return SequenceParser(left + right)
}

/** Combines this parser and [other] into a sequence, equivalent to calling [seq]. */
operator fun Parser<*>.plus(other: Parser<*>): SequenceParser<Any?> = this seq other

/** A parser that parses a sequence of parsers. */
class SequenceParser<R>(children: Iterable<Parser<R>>) : ListParser<R, List<R>>(children), SequentialParser {
  constructor(vararg children: Parser<R>) : this(children.toList())

  override fun parseOn(input: Input): Output<List<R>> {
    var current = input
    val elements = ArrayList<R>(parsers.size)
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

  override fun fastParseOn(buffer: String, position: Int): Int {
    var pos = position
    for (parser in parsers) {
      pos = parser.fastParseOn(buffer, pos)
      if (pos < 0) return -1
    }
    return pos
  }

  override fun copy(): SequenceParser<R> = SequenceParser(parsers)
}