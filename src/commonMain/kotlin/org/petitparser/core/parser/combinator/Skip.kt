package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.Parser

/**
 * Returns a parser that consumes input [before] and [after] the receiver, but discards the parse
 * results of [before] and [after] and only returns the result of the receiver.
 */
fun <R> Parser<R>.skip(before: Parser<*>? = null, after: Parser<*>? = null): Parser<R> =
  if (before == null) if (after == null) this
  else seqMap(this, after) { value, _ -> value }
  else if (after == null) seqMap(before, this) { _, value -> value }
  else seqMap(before, this, after) { _, value, _ -> value }