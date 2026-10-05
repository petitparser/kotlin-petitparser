package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that returns a constant [value] on success. */
fun <R> Parser<*>.constant(value: R): Parser<R> = ConstantParser(this, value)

/** A parser that returns a constant [value] on success. */
class ConstantParser<T, out R>(
  delegate: Parser<T>,
  val value: R,
) : DelegateParser<T, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> result.success(value)
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is ConstantParser<*, *> && value == other.value

  override fun copy(): ConstantParser<T, R> = ConstantParser(delegate, value)
}
