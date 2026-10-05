package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that extracts the element at the provided [index]. */
fun <R> Parser<List<R>>.pick(index: Int): Parser<R> = PickParser(this, index)

/** Returns a parser that extracts elements at the specified [indices] range. */
fun <R> Parser<List<R>>.slice(indices: IntRange): Parser<List<R>> =
  map { value -> value.slice(indices) }

/** Returns a parser that extracts elements at specified [indices]. */
fun <R> Parser<List<R>>.slice(indices: Iterable<Int>): Parser<List<R>> =
  map { value -> value.slice(indices) }

/** Returns a parser that extracts elements at specified [indices]. */
fun <R> Parser<List<R>>.slice(vararg indices: Int): Parser<List<R>> =
  slice(indices.toList())

/** A parser that extracts the element at [index] from a parsed list. */
class PickParser<R>(
  delegate: Parser<List<R>>,
  val index: Int,
) : DelegateParser<List<R>, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> {
        val value = result.value
        result.success(value[if (index < 0) value.size + index else index])
      }
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun toString(): String = "${super.toString()}[$index]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is PickParser<*> && index == other.index

  override fun copy(): PickParser<R> = PickParser(delegate, index)
}
