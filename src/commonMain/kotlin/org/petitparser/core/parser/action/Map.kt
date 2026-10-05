package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that evaluates [callback] on success of the receiver. */
fun <T, R> Parser<T>.map(callback: (T) -> R): Parser<R> =
  MapParser(this, callback, false)

/** Returns a parser that evaluates [callback] on success of the receiver. */
fun <T, R> Parser<T>.map(hasSideEffects: Boolean, callback: (T) -> R): Parser<R> =
  MapParser(this, callback, hasSideEffects)

/** A parser that performs a transformation with a given [callback] on the successful parse result. */
class MapParser<T, out R>(
  delegate: Parser<T>,
  val callback: (T) -> R,
  val hasSideEffects: Boolean = false,
) : DelegateParser<T, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    return when (val result = delegate.parseOn(input)) {
      is Output.Success -> result.success(callback(result.value))
      is Output.Failure -> result
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    if (hasSideEffects) super.fastParseOn(buffer, position) else delegate.fastParseOn(buffer, position)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is MapParser<*, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun copy(): MapParser<T, R> = MapParser(delegate, callback, hasSideEffects)
}
