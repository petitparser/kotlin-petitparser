package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** A list of [elements] and its [separators]. */
data class SeparatedList<R, S>(val elements: List<R>, val separators: List<S>)

/** Returns a parser that accepts the receiver zero or more times, separated by [separator]. */
fun <R, S> Parser<R>.starSeparated(separator: Parser<S>) =
  repeatSeparated(separator, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times, separated by [separator]. */
fun <R, S> Parser<R>.plusSeparated(separator: Parser<S>) =
  repeatSeparated(separator, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times, separated by [separator]. */
fun <R, S> Parser<R>.timesSeparated(separator: Parser<S>, count: Int) =
  repeatSeparated(separator, min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times, separated by [separator]. */
fun <R, S> Parser<R>.repeatSeparated(separator: Parser<S>, min: Int, max: Int) =
  object : Parser<SeparatedList<R, S>> {
    override val children get() = listOf(this@repeatSeparated, separator)
    override fun parseOn(input: Input): Output<SeparatedList<R, S>> {
      var current = input
      val elements = mutableListOf<R>()
      val separators = mutableListOf<S>()
      while (elements.size < min) {
        if (elements.isNotEmpty()) {
          when (val separation = separator.parseOn(current)) {
            is Output.Success -> {
              current = separation
              separators.add(separation.value)
            }
            is Output.Failure -> return separation
          }
        }
        when (val result = this@repeatSeparated.parseOn(current)) {
          is Output.Success -> {
            current = result
            elements.add(result.value)
          }
          is Output.Failure -> return result
        }
      }
      while (elements.size < max) {
        val previous = current
        if (elements.isNotEmpty()) {
          when (val separation = separator.parseOn(current)) {
            is Output.Success -> {
              current = separation
              separators.add(separation.value)
            }
            is Output.Failure -> return current.success(SeparatedList(elements, separators))
          }
        }
        when (val result = this@repeatSeparated.parseOn(current)) {
          is Output.Success -> {
            current = result
            elements.add(result.value)
          }
          is Output.Failure -> {
            if (elements.isNotEmpty()) separators.removeLast()
            return previous.success(SeparatedList(elements, separators))
          }
        }
      }
      return current.success(SeparatedList(elements, separators))
    }
  }

