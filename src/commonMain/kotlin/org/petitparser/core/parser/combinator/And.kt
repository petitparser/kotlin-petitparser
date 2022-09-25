package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that succeeds whenever the receiver does, but never consumes input. */
fun <R> Parser<R>.and() = object : Parser<R> {
  override fun parseOn(context: Context) = when (val result = this@and.parseOn(context)) {
    is Success -> context.success(result.value)
    else -> result
  }
}