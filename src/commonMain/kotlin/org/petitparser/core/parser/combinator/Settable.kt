package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.misc.failure

/** Returns a parser that is not defined, but that can be set at a later point in time. */
fun <R> undefined(message: String = "undefined parser"): SettableParser<R> =
  failure<R>(message).settable()

/** Returns a parser that points to the receiver, but can be changed to delegate somewhere else. */
fun <R> Parser<R>.settable(): SettableParser<R> = SettableParser(this)

/** A parser that dispatches to a [delegate]. */
class SettableParser<R>(delegate: Parser<R>) : DelegateParser<R, R>(delegate) {
  /** Sets the receiver to delegate to [parser]. */
  fun set(parser: Parser<R>) {
    delegate = parser
  }

  override fun parseOn(input: Input): Output<R> = delegate.parseOn(input)

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun copy(): SettableParser<R> = SettableParser(delegate)
}


