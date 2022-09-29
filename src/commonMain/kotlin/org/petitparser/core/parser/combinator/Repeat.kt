package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the receiver zero or more times. */
fun <R> Parser<R>.star() = repeat(min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun <R> Parser<R>.plus() = repeat(min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun <R> Parser<R>.repeat(count: Int) = repeat(min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun <R> Parser<R>.repeat(min: Int, max: Int) = object : Parser<List<R>> {
  override val children: List<Parser<*>> get() = listOf(this@repeat)
  override fun parseOn(input: Input): Output<List<R>> {
    var current = input
    val elements = mutableListOf<R>()
    while (elements.size < min) {
      when (val result = this@repeat.parseOn(current)) {
        is Output.Success -> {
          elements.add(result.value)
          current = result
        }
        is Output.Failure -> return result.failure(result.message)
      }
    }
    while (elements.size < max) {
      when (val result = this@repeat.parseOn(current)) {
        is Output.Success -> {
          elements.add(result.value)
          current = result
        }
        is Output.Failure -> return current.success(elements)
      }
    }
    return current.success(elements)
  }
}

