package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.star(): PossessiveRepeatingParser<R> = repeat(min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.plus(): PossessiveRepeatingParser<R> = repeat(min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun <R> Parser<R>.times(count: Int): PossessiveRepeatingParser<R> = repeat(min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.repeat(min: Int, max: Int = min): PossessiveRepeatingParser<R> =
  PossessiveRepeatingParser(this, min, max)

/**
 * A greedy parser that repeatedly parses between [min] and [max] instances of its delegate.
 */
class PossessiveRepeatingParser<R>(
  delegate: Parser<R>,
  min: Int,
  max: Int = min,
) : RepeatingParser<R, List<R>>(delegate, min, max) {
  override fun parseOn(input: Input): Output<List<R>> {
    val elements = mutableListOf<R>()
    var current = input
    while (elements.size < min) {
      when (val result = delegate.parseOn(current)) {
        is Output.Failure -> return result
        is Output.Success -> {
          check(current.position < result.position) { "$delegate must always consume" }
          elements.add(result.value)
          current = result
        }
      }
    }
    while (elements.size < max) {
      when (val result = delegate.parseOn(current)) {
        is Output.Failure -> break
        is Output.Success -> {
          check(current.position < result.position) { "$delegate must always consume" }
          elements.add(result.value)
          current = result
        }
      }
    }
    return current.success(elements)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    var count = 0
    var current = position
    while (count < min) {
      val result = delegate.fastParseOn(buffer, current)
      if (result < 0) return -1
      check(current < result) { "$delegate must always consume" }
      current = result
      count++
    }
    while (count < max) {
      val result = delegate.fastParseOn(buffer, current)
      if (result < 0) break
      check(current < result) { "$delegate must always consume" }
      current = result
      count++
    }
    return current
  }

  override fun copy(): PossessiveRepeatingParser<R> =
    PossessiveRepeatingParser(delegate, min, max)
}
