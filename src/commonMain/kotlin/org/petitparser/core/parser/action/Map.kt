package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser returns the result of [handler] on success of the receiver. */
fun <T, R> Parser<T>.map(handler: (T) -> R) = object : Parser<R> {
  override fun parseOn(context: Context) = when (val result = this@map.parseOn(context)) {
    is Success -> result.success(handler(result.value))
    else -> result.failure(result.message)
  }
}

