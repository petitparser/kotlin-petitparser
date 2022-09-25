package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Failure
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.star() = repeat(min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.plus() = repeat(min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun <R> Parser<R>.times(count: Int) = repeat(min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.repeat(min: Int, max: Int) = object : Parser<List<R>> {
  override fun parseOn(context: Context): Result<List<R>> {
    var current = context
    val elements = mutableListOf<R>()
    while (elements.size < min) {
      val result = this@repeat.parseOn(current)
      if (result is Failure) {
        return result.failure(result.message)
      }
      elements.add(result.value)
      current = result
    }
    while (elements.size < max) {
      val result = this@repeat.parseOn(current)
      if (result is Failure) {
        return current.success(elements)
      }
      elements.add(result.value)
      current = result
    }
    return current.success(elements)
  }
}

