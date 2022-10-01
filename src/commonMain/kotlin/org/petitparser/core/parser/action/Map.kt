package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser returns the result of [handler] on success of the receiver. */
fun <T, R> Parser<T>.map(handler: (T) -> R) = object : Parser<R> {
  override val children = listOf(this@map)
  override fun parseOn(input: Input) = when (val result = this@map.parseOn(input)) {
    is Output.Success -> result.success(handler(result.value))
    is Output.Failure -> result.failure(result.message)
  }
}

