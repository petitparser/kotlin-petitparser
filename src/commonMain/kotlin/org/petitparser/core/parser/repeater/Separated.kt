package org.petitparser.core.parser.repeater

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.seq

/** Returns a parser that consumes the receiver one or more times separated by the [separator] parser. */
@Suppress("UNCHECKED_CAST")
fun <R> Parser<R>.separatedBy(
  separator: Parser<R>,
  includeSeparators: Boolean = true,
) = seq(
  this,
  seq(separator, this).star(),
).map {
  val result = mutableListOf(it[0] as R)
  for (tuple in it[1] as List<List<R>>) {
    if (includeSeparators) result.add(tuple[0])
    result.add(tuple[1])
  }
  return@map result
}