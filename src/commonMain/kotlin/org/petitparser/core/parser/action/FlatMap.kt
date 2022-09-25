package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser uses the resulting parser of [handler] on success of the receiver. */
fun <T, R> Parser<T>.flatMap(handler: (T) -> Parser<R>) = object : Parser<R> {
  override fun parseOn(context: Context) = when (val result = this@flatMap.parseOn(context)) {
    is Success -> handler(result.value).parseOn(context)
    else -> result.failure(result.message)
  }
}