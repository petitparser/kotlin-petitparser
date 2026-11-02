package org.petitparser.core.matcher

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Tests if the [input] can be successfully parsed. */
fun <R> Parser<R>.accept(input: CharSequence, start: Int = 0): Boolean =
  fastParseOn(input.toString(), start) != -1

/**
 * Returns a lazy sequence over all successful parse results over the provided [input].
 */
fun <R> Parser<R>.matches(
  input: CharSequence,
  overlapping: Boolean = false,
  start: Int = 0,
): Sequence<R> = sequence {
  val string = input.toString()
  var currentStart = start
  while (currentStart <= string.length) {
    val end = fastParseOn(string, currentStart)
    if (end < 0) {
      currentStart++
    } else {
      when (val result = parseOn(Input.Impl(string, currentStart))) {
        is Output.Success -> {
          yield(result.value)
          if (overlapping || currentStart == end) {
            currentStart++
          } else {
            currentStart = end
          }
        }
        is Output.Failure -> currentStart++
      }
    }
  }
}

/**
 * Returns a lazy sequence over all non-overlapping successful parse results.
 */
fun <R> Parser<R>.matchesSkipping(input: CharSequence, start: Int = 0): Sequence<R> =
  matches(input, overlapping = false, start = start)

/** Represents a match found by [ParserPattern]. */
data class ParserMatch(
  val pattern: ParserPattern,
  val input: String,
  val start: Int,
  val end: Int,
) {
  val group: String get() = input.substring(start, end)
}

/** A pattern matcher backed by a [Parser]. */
class ParserPattern(val parser: Parser<*>) {
  fun matchAsPrefix(input: CharSequence, start: Int = 0): ParserMatch? {
    val string = input.toString()
    val end = parser.fastParseOn(string, start)
    return if (end < 0) null else ParserMatch(this, string, start, end)
  }

  fun allMatches(input: CharSequence, start: Int = 0): Sequence<ParserMatch> = sequence {
    val string = input.toString()
    var current = start
    while (current <= string.length) {
      val end = parser.fastParseOn(string, current)
      if (end < 0) {
        current++
      } else {
        yield(ParserMatch(this@ParserPattern, string, current, end))
        if (current == end) {
          current++
        } else {
          current = end
        }
      }
    }
  }
}

/** Converts this [Parser] into a [ParserPattern]. */
fun <R> Parser<R>.toPattern(): ParserPattern = ParserPattern(this)
