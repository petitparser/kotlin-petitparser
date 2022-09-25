package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Context
import org.petitparser.core.context.Failure
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.lazyStar(limit: Parser<Nothing>) =
  lazyRepeat(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.lazyPlus(limit: Parser<Nothing>) =
  lazyRepeat(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun <R> Parser<R>.lazyRepeat(limit: Parser<Nothing>, count: Int) =
  lazyRepeat(limit, min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.lazyRepeat(limit: Parser<Nothing>, min: Int, max: Int) =
  object : Parser<List<R>> {
    override fun parseOn(context: Context): Result<List<R>> {
      var current = context
      val elements = mutableListOf<R>()
      while (elements.size < min) {
        var result = this@lazyRepeat.parseOn(current)
        if (result is Failure) {
          return result.failure(result.message)
        }
        elements.add(result.value)
        current = result
      }
      while (true) {
        var limiter = limit.parseOn(current)
        if (limiter is Success) {
          return current.success(elements)
        } else {
          if (elements.size >= max) {
            return limiter.failure(limiter.message)
          }
          var result = this@lazyRepeat.parseOn(current)
          if (result is Failure) {
            return limiter.failure(limiter.message)
          }
          elements.add(result.value)
          current = result
        }
      }
    }
  }

