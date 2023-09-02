package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that captures a continuation function and passes it together with the current context into the handler. */
fun <T, R> Parser<T>.callCC(handler: (continuation: (Input) -> Output<T>, input: Input) -> Output<R>) =
  Parser { input -> handler(this@callCC::parseOn, input) }