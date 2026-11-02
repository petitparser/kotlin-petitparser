package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that parses the receiver zero or more times until it reaches a [limit]. */
fun <R> Parser<R>.starGreedy(limit: Parser<*>): GreedyRepeatingParser<R> =
  repeatGreedy(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that parses the receiver one or more times until it reaches a [limit]. */
fun <R> Parser<R>.plusGreedy(limit: Parser<*>): GreedyRepeatingParser<R> =
  repeatGreedy(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that parses the receiver between [min] and [max] times until it reaches a [limit]. */
fun <R> Parser<R>.repeatGreedy(
  limit: Parser<*>,
  min: Int,
  max: Int = min,
): GreedyRepeatingParser<R> = GreedyRepeatingParser(this, limit, min, max)

/**
 * A greedy repeating parser that aggressively consumes as much input as possible and then backtracks
 * to meet the [limit] condition.
 */
class GreedyRepeatingParser<R>(
  delegate: Parser<R>,
  limit: Parser<*>,
  min: Int,
  max: Int = min,
) : LimitedRepeatingParser<R>(delegate, limit, min, max) {
  override fun parseOn(input: Input): Output<List<R>> {
    var current = input
    val elements = mutableListOf<R>()
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
    val contexts = mutableListOf(current)
    while (elements.size < max) {
      when (val result = delegate.parseOn(current)) {
        is Output.Failure -> break
        is Output.Success -> {
          check(current.position < result.position) { "$delegate must always consume" }
          elements.add(result.value)
          current = result
          contexts.add(result)
        }
      }
    }
    while (true) {
      when (val limiter = limit.parseOn(contexts.last())) {
        is Output.Failure -> {
          if (elements.isEmpty()) return limiter
          contexts.removeLast()
          elements.removeLast()
          if (contexts.isEmpty()) return limiter
        }
        is Output.Success -> return contexts.last().success(elements)
      }
    }
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
    val positions = mutableListOf(current)
    while (count < max) {
      val result = delegate.fastParseOn(buffer, current)
      if (result < 0) break
      check(current < result) { "$delegate must always consume" }
      positions.add(result)
      current = result
      count++
    }
    while (true) {
      val limiter = limit.fastParseOn(buffer, positions.last())
      if (limiter < 0) {
        if (count == 0) return -1
        positions.removeLast()
        count--
        if (positions.isEmpty()) return -1
      } else {
        return positions.last()
      }
    }
  }

  override fun copy(): GreedyRepeatingParser<R> =
    GreedyRepeatingParser(delegate, limit, min, max)
}
