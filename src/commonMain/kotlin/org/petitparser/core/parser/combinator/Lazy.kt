package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.lazyStar(limit: Parser<*>) = lazyRepeat(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.lazyPlus(limit: Parser<*>) = lazyRepeat(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.lazyRepeat(limit: Parser<*>, min: Int, max: Int = min) =
  object : Parser<List<R>> {
    override val children: List<Parser<*>> get() = listOf(this@lazyRepeat, limit)
    override fun parseOn(input: Input): Output<List<R>> {
      var current = input
      val elements = mutableListOf<R>()
      while (elements.size < min) {
        when (val result = this@lazyRepeat.parseOn(current)) {
          is Output.Success -> {
            elements.add(result.value)
            current = result
          }
          is Output.Failure -> return result.failure(result.message)
        }
      }
      while (true) {
        when (val limiter = limit.parseOn(current)) {
          is Output.Success -> return current.success(elements)
          is Output.Failure -> {
            if (elements.size >= max) {
              return limiter.failure(limiter.message)
            }
            when (val result = this@lazyRepeat.parseOn(current)) {
              is Output.Success -> {
                elements.add(result.value)
                current = result
              }
              is Output.Failure -> return limiter.failure(limiter.message)
            }
          }
        }
      }
    }
  }

