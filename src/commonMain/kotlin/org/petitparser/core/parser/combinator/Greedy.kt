package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Failure
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.greedyStar(limit: Parser<*>) =
  greedyRepeat(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.greedyPlus(limit: Parser<*>) =
  greedyRepeat(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.greedyRepeat(limit: Parser<*>, min: Int, max: Int = min) =
  object : Parser<List<R>> {
    override fun parseOn(context: Context): Result<List<R>> {
      var current = context
      val elements = mutableListOf<R>()
      while (elements.size < min) {
        val result = this@greedyRepeat.parseOn(current)
        if (result is Failure) {
          return result.failure(result.message)
        }
        elements.add(result.value)
        current = result
      }
      val contexts = mutableListOf(current)
      while (elements.size < max) {
        val result = this@greedyRepeat.parseOn(current)
        if (result is Failure) {
          break
        }
        elements.add(result.value)
        contexts.add(result)
        current = result
      }
      while (true) {
        val limiter = limit.parseOn(contexts.last())
        if (limiter is Success) {
          return contexts.last().success(elements)
        }
        if (elements.isEmpty()) {
          return limiter.failure(limiter.message)
        }
        contexts.removeLast()
        elements.removeLast()
        if (contexts.isEmpty()) {
          return limiter.failure(limiter.message)
        }
      }
    }
  }

