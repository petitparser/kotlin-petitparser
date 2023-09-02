package org.petitparser.core.parser.action

import org.petitparser.core.context.Output
import org.petitparser.core.context.Token
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that returns a [Token]. */
fun <R> Parser<R>.token() =
  Parser { input ->
    when (val result = this@token.parseOn(input)) {
      is Output.Success -> result.success(
        Token(
          result.value,
          input.buffer,
          input.position,
          result.position,
        )
      )

      is Output.Failure -> result
    }
  }