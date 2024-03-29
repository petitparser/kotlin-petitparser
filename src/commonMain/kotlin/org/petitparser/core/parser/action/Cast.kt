package org.petitparser.core.parser.action

import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that casts its success result to [R]. */
fun <T, R> Parser<T>.cast() = Parser { input ->
  @Suppress("UNCHECKED_CAST")
  when (val result = this@cast.parseOn(input)) {
    is Output.Success -> result.success(result.value as R)
    is Output.Failure -> result
  }
}
