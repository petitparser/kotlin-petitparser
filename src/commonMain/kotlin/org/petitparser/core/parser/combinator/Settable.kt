package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.misc.failure

/** Returns a parser that is not defined, but that can be set at a later point in time. */
fun <R> undefined(message: String = "undefined parser") = failure<R>(message).settable()

/** Returns a parser that points to the receiver, but can be changed to delegate somewhere else. */
fun <R> Parser<R>.settable() = Settable(this)

/** A parser that dispatches to a [delegate]. */
class Settable<R>(var delegate: Parser<R>) : Parser<R> {
  override fun parseOn(input: Input): Output<R> = delegate.parseOn(input)
}

