package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser

/** Returns a parser that captures a continuation function and passes it together with the current context into the handler. */
fun <T, R> Parser<T>.callCC(handler: ((Context) -> Result<T>, Context) -> Result<R>) =
  object : Parser<R> {
    override fun parseOn(context: Context) = handler(this@callCC::parseOn, context)
  }