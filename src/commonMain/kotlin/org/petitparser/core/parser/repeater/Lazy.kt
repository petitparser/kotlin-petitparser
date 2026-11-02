package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that parses the receiver zero or more times until it reaches a [limit]. */
fun <R> Parser<R>.starLazy(limit: Parser<*>): LazyRepeatingParser<R> =
  repeatLazy(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that parses the receiver one or more times until it reaches a [limit]. */
fun <R> Parser<R>.plusLazy(limit: Parser<*>): LazyRepeatingParser<R> =
  repeatLazy(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that parses the receiver between [min] and [max] times until it reaches a [limit]. */
fun <R> Parser<R>.repeatLazy(
  limit: Parser<*>,
  min: Int,
  max: Int = min,
): LazyRepeatingParser<R> = LazyRepeatingParser(this, limit, min, max)

/**
 * A lazy repeating parser that limits its consumption to meet the [limit] condition as early as possible.
 */
class LazyRepeatingParser<R>(
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
    while (true) {
      when (val limiter = limit.parseOn(current)) {
        is Output.Failure -> {
          if (elements.size >= max) return limiter
          when (val result = delegate.parseOn(current)) {
            is Output.Failure -> return limiter
            is Output.Success -> {
              check(current.position < result.position) { "$delegate must always consume" }
              elements.add(result.value)
              current = result
            }
          }
        }
        is Output.Success -> return current.success(elements)
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
    while (true) {
      val limiter = limit.fastParseOn(buffer, current)
      if (limiter < 0) {
        if (count >= max) return -1
        val result = delegate.fastParseOn(buffer, current)
        if (result < 0) return -1
        check(current < result) { "$delegate must always consume" }
        current = result
        count++
      } else {
        return current
      }
    }
  }

  override fun copy(): LazyRepeatingParser<R> =
    LazyRepeatingParser(delegate, limit, min, max)
}
