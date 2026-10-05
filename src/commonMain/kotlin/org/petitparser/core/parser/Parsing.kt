package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.matcher.matches as matcherMatches

/** Parses the provided [input]. */
fun <R> Parser<R>.parse(input: String, start: Int = 0): Output<R> =
  parseOn(Input(input, start))

/** Tests if the [input] can be successfully parsed. */
fun <R> Parser<R>.accept(input: String, start: Int = 0): Boolean =
  fastParseOn(input, start) != -1

/** Returns a lazy sequence over all successful parse results over the provided [input]. */
fun <R> Parser<R>.matches(
  input: String,
  overlapping: Boolean = false,
  start: Int = 0,
): Sequence<R> = matcherMatches(input, overlapping, start)