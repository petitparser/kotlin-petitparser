package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.greedyStar(limit: Parser<*>) = greedyRepeat(limit, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.greedyPlus(limit: Parser<*>) = greedyRepeat(limit, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.greedyRepeat(limit: Parser<*>, min: Int, max: Int = min) =
  object : Parser<List<R>> {
    override val children get() = listOf(this@greedyRepeat, limit)
    override fun parseOn(input: Input): Output<List<R>> {
      var current = input
      val elements = mutableListOf<R>()
      while (elements.size < min) {
        when (val result = this@greedyRepeat.parseOn(current)) {
          is Output.Success -> {
            elements.add(result.value)
            current = result
          }
          is Output.Failure -> return result.failure(result.message)
        }
      }
      val contexts = mutableListOf(current)
      while (elements.size < max) {
        when (val result = this@greedyRepeat.parseOn(current)) {
          is Output.Success -> {
            elements.add(result.value)
            contexts.add(result)
            current = result
          }
          is Output.Failure -> break
        }
      }
      while (true) {
        when (val limiter = limit.parseOn(contexts.last())) {
          is Output.Success -> return contexts.last().success(elements)
          is Output.Failure -> {
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
    }
  }

