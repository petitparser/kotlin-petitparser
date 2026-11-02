package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that casts its success result to [R]. */
fun <R> Parser<*>.cast(): Parser<R> = CastParser(this)

/** A parser that casts a successful parse result to [R]. */
class CastParser<T, out R>(delegate: Parser<T>) : DelegateParser<T, R>(delegate) {
  @Suppress("UNCHECKED_CAST")
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> result.success(result.value as R)
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun copy(): CastParser<T, R> = CastParser(delegate)
}
