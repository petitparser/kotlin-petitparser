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
): ChoiceParser<R> = ChoiceParser(parsers.toList(), failureJoiner)

/** Returns a parser that accepts the result of the first succeeding of [parsers]. */
fun <R> or(
  parsers: Iterable<Parser<R>>,
  failureJoiner: FailureJoiner = ::selectLast,
): ChoiceParser<R> = ChoiceParser(parsers, failureJoiner)

/** Combines this iterable of parsers into a single [ChoiceParser]. */
fun <R> Iterable<Parser<R>>.toChoiceParser(
  failureJoiner: FailureJoiner = ::selectLast,
): ChoiceParser<R> = ChoiceParser(this, failureJoiner)

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix operator fun <R> Parser<R>.div(other: Parser<R>): ChoiceParser<R> = or(other)

/** Returns a parser that accepts the parse result of this or [other] parser. */
infix fun <R> Parser<R>.or(other: Parser<R>): ChoiceParser<R> = or(other, null)

/** Returns a parser that accepts the parse result of this or [other] parser, using [failureJoiner]. */
@Suppress("UNCHECKED_CAST")
fun <R> Parser<R>.or(
  other: Parser<R>,
  failureJoiner: FailureJoiner?,
): ChoiceParser<R> {
  val left = if (this is ChoiceParser<*>) children as List<Parser<R>> else listOf(this)
  val right = if (other is ChoiceParser<*>) other.children as List<Parser<R>> else listOf(other)
  val joiner = failureJoiner ?: if (this is ChoiceParser<*>) this.failureJoiner else ::selectLast
  return ChoiceParser(left + right, joiner)
}

/** A parser that uses the first parser that succeeds. */
class ChoiceParser<R>(
  children: Iterable<Parser<R>>,
  val failureJoiner: FailureJoiner = ::selectLast,
) : ListParser<R, R>(children) {
  constructor(
    vararg children: Parser<R>,
    failureJoiner: FailureJoiner = ::selectLast,
  ) : this(children.toList(), failureJoiner)

  init {
    require(parsers.isNotEmpty()) { "Choice parser cannot be empty" }
  }

  override fun parseOn(input: Input): Output<R> {
    val first = parsers[0].parseOn(input)
    if (first !is Output.Failure) return first
    var failure: Output.Failure = first
    for (i in 1 until parsers.size) {
      val result = parsers[i].parseOn(input)
      if (result !is Output.Failure) return result
      failure = failureJoiner(failure, result)
    }
    return failure
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    for (parser in parsers) {
      val result = parser.fastParseOn(buffer, position)
      if (result >= 0) return result
    }
    return -1
  }

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is ChoiceParser<*> &&
      failureJoiner == other.failureJoiner

  override fun copy(): ChoiceParser<R> = ChoiceParser(parsers, failureJoiner)
}

