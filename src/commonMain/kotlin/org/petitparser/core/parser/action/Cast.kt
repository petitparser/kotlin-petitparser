package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that casts its success result to [R]. */
fun <T, R> Parser<T>.cast() = object : Parser<R> {
  @Suppress("UNCHECKED_CAST")
  override fun parseOn(context: Context) = when (val result = this@cast.parseOn(context)) {
    is Success -> result.success(result.value as R)
    else -> result.failure(result.message)
  }
}
