package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that discards the result of the receiver and returns the sub-string this parser consumes. */
fun <R> Parser<R>.flatten() = object : Parser<String> {
  override fun parseOn(context: Context) = when (val result = this@flatten.parseOn(context)) {
    is Success -> result.success(context.buffer.substring(context.position, result.position))
    else -> result.failure(result.message)
  }
}