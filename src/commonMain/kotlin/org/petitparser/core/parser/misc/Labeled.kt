package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/**
 * Returns a parser that simply defers to its delegate, but has a [name] for
 * debugging purposes.
 */
fun <R> Parser<R>.label(name: String): LabeledParser<R> = LabeledParser(this, name)

/**
 * Returns a parser that simply defers to its delegate, but has a [name] for
 * debugging purposes.
 */
fun <R> Parser<R>.labeled(name: String): LabeledParser<R> = label(name)

/**
 * A parser that always defers to its delegate, but that also holds a label
 * for debugging purposes.
 */
class LabeledParser<R>(
  delegate: Parser<R>,
  val label: String,
) : DelegateParser<R, R>(delegate) {
  override fun parseOn(input: Input): Output<R> = delegate.parseOn(input)

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun toString(): String = "${super.toString()}[$label]"

  override fun copy(): LabeledParser<R> = LabeledParser(delegate, label)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is LabeledParser<*> && label == other.label
}
