package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.Token
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/** Returns a parser that returns a [Token]. */
fun <R> Parser<R>.token(): Parser<Token<R>> = TokenParser(this)

/** A parser that creates a token of the result its delegate parses. */
class TokenParser<R>(delegate: Parser<R>) : DelegateParser<R, Token<R>>(delegate) {
  override fun parseOn(input: Input): Output<Token<R>> {
    return when (val result = delegate.parseOn(input)) {
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

  override fun fastParseOn(buffer: String, position: Int): Int =
    delegate.fastParseOn(buffer, position)

  override fun copy(): TokenParser<R> = TokenParser(delegate)
}