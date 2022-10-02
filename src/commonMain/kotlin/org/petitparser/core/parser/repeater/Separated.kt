package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times, separated by [separator]. */
fun <R> Parser<R>.separatedStar(separator: Parser<*>) =
  separatedRepeat(separator, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times, separated by [separator]. */
fun <R> Parser<R>.separatedPlus(separator: Parser<*>) =
  separatedRepeat(separator, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times, separated by [separator]. */
fun <R> Parser<R>.separatedRepeat(separator: Parser<*>, count: Int) =
  separatedRepeat(separator, min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times, separated by [separator]. */
fun <R> Parser<R>.separatedRepeat(separator: Parser<*>, min: Int, max: Int) =
  object : Parser<List<R>> {
    override val children get() = listOf(this@separatedRepeat, separator)
    override fun parseOn(input: Input): Output<List<R>> {
      var current = input
      val elements = mutableListOf<R>()
      while (elements.size < min) {
        if (elements.isNotEmpty()) {
          when (val separation = separator.parseOn(current)) {
            is Output.Success -> current = separation
            is Output.Failure -> return separation.failure(separation.message)
          }
        }
        when (val result = this@separatedRepeat.parseOn(current)) {
          is Output.Success -> {
            elements.add(result.value)
            current = result
          }
          is Output.Failure -> return result.failure(result.message)
        }
      }
      while (elements.size < max) {
        var previous = current
        if (elements.isNotEmpty()) {
          when (val separation = separator.parseOn(current)) {
            is Output.Success -> current = separation
            is Output.Failure -> return current.success(elements)
          }
        }
        when (val result = this@separatedRepeat.parseOn(current)) {
          is Output.Success -> {
            elements.add(result.value)
            current = result
          }
          is Output.Failure -> return previous.success(elements)
        }
      }
      return current.success(elements)
    }
  }

