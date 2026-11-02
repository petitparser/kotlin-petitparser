package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that permutes elements of the parsed list according to [indices]. */
fun <R> Parser<List<R>>.permute(indices: List<Int>): Parser<List<R>> =
  PermuteParser(this, indices)

/** Returns a parser that permutes elements of the parsed list according to [indices]. */
fun <R> Parser<List<R>>.permute(indices: Iterable<Int>): Parser<List<R>> =
  PermuteParser(this, indices.toList())

/** Returns a parser that permutes elements of the parsed list according to [indices]. */
fun <R> Parser<List<R>>.permute(vararg indices: Int): Parser<List<R>> =
  PermuteParser(this, indices.toList())

/** A parser that permutes elements of the parsed list according to [indices]. */
class PermuteParser<R>(
  delegate: Parser<List<R>>,
  val indices: List<Int>,
) : DelegateParser<List<R>, List<R>>(delegate) {
  override fun parseOn(input: Input): Output<List<R>> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> {
        val value = result.value
        val size = value.size
        val values = ArrayList<R>(indices.size)
        for (i in indices.indices) {
          val index = indices[i]
          values.add(value[if (index < 0) size + index else index])
        }
        result.success(values)
      }
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun toString(): String =
    "${super.toString()}[${indices.joinToString(", ")}]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is PermuteParser<*> && indices == other.indices

  override fun copy(): PermuteParser<R> = PermuteParser(delegate, indices)
}
