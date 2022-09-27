package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns new parser that accepts the receiver, otherwise returns `null`. */
fun <R> Parser<R>.optional() = optional(null)

/** Returns new parser that accepts the receiver, otherwise returns [value]. */
fun <R> Parser<R>.optional(value: R) = object : Parser<R> {
  override fun parseOn(input: Input) = when (val result = this@optional.parseOn(input)) {
    is Output.Success -> result.success(result.value)
    is Output.Failure -> input.success(value)
  }
}