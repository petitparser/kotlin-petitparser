package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns new parser that accepts the receiver, otherwise returns `null`. */
fun <R> Parser<R>.optional() = optional(null)

/** Returns new parser that accepts the receiver, otherwise returns [value]. */
fun <R> Parser<R>.optional(value: R) = object : Parser<R> {
  override fun parseOn(context: Context) = when (val result = this@optional.parseOn(context)) {
    is Success -> result.success(result.value)
    else -> context.success(value)
  }
}