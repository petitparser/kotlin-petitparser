package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and succeeds. */
fun epsilon(): EpsilonParser<Unit> = EpsilonParser(Unit)

/** Returns a parser that consumes nothing and succeeds with [value]. */
fun <R> epsilon(value: R): EpsilonParser<R> = EpsilonParser(value)

/** Returns a parser that consumes nothing and succeeds with [value]. */
fun <R> epsilonWith(value: R): EpsilonParser<R> = EpsilonParser(value)

/** Returns a parser that consumes nothing and succeeds. */
@Deprecated("Use epsilon() or epsilonWith(value) instead", ReplaceWith("epsilon()"))
fun success(): EpsilonParser<Unit> = epsilon()

/** Returns a parser that consumes nothing and succeeds with a [value]. */
@Deprecated("Use epsilon() or epsilonWith(value) instead", ReplaceWith("epsilon(value)"))
fun <R> success(value: R): EpsilonParser<R> = epsilonWith(value)

/** A parser that consumes nothing and succeeds. */
class EpsilonParser<out R>(
  val value: R,
) : Parser<R> {
  val result: R
    get() = value

  override fun parseOn(input: Input): Output<R> = input.success(value)

  override fun fastParseOn(buffer: String, position: Int): Int = position

  override fun toString(): String = "${this::class.simpleName}[$value]"

  override fun copy(): EpsilonParser<R> = EpsilonParser(value)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is EpsilonParser<*> && value == other.value
}
