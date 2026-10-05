package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that casts its successful list result to `List<R>`. */
fun <R> Parser<*>.castList(): Parser<List<R>> = CastListParser(this)

/** A parser that casts a successful list result to `List<R>`. */
class CastListParser<T, out R>(delegate: Parser<T>) : DelegateParser<T, List<R>>(delegate) {
  @Suppress("UNCHECKED_CAST")
  override fun parseOn(input: Input): Output<List<R>> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> result.success(result.value as List<R>)
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun copy(): CastListParser<T, R> = CastListParser(delegate)
}
