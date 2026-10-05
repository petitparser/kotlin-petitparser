package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

typealias ContinuationFunction<T> = (Input) -> Output<T>
typealias ContinuationHandler<T, R> = (continuation: ContinuationFunction<T>, input: Input) -> Output<R>

/** Returns a parser that captures a continuation function and passes it together with the current context into the handler. */
fun <T, R> Parser<T>.callCC(handler: ContinuationHandler<T, R>): Parser<R> =
  ContinuationParser(this, handler)

/** A parser that captures a continuation function and passes it into [handler]. */
class ContinuationParser<T, out R>(
  delegate: Parser<T>,
  val handler: ContinuationHandler<T, R>,
) : DelegateParser<T, R>(delegate) {
  override fun parseOn(input: Input): Output<R> = handler(delegate::parseOn, input)

  override fun copy(): ContinuationParser<T, R> = ContinuationParser(delegate, handler)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is ContinuationParser<*, *> && handler == other.handler
}
