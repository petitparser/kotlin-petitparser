package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Failure
import org.petitparser.core.parser.Parser

/** Returns a parser that succeeds with the [Failure] whenever the receiver fails, but never consumes input. */
fun <R> Parser<R>.not(message: String = "no success expected") = object : Parser<Failure<R>> {
  override fun parseOn(context: Context) = when (val result = this@not.parseOn(context)) {
    is Failure -> context.success(result)
    else -> context.failure(message)
  }
}