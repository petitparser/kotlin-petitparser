package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.utils.SequentialParser

/** A list of [elements] and its [separators]. */
data class SeparatedList<R, S>(val elements: List<R>, val separators: List<S>) {
  init {
    require(maxOf(0, elements.size - 1) == separators.size) {
      "Inconsistent number of elements ($elements) and separators ($separators)"
    }
  }

  /** An iterable sequence over the [elements] and interleaved [separators] in order of appearance. */
  val sequential: Sequence<Any?>
    get() = sequence {
      for (i in elements.indices) {
        yield(elements[i])
        if (i < separators.size) {
          yield(separators[i])
        }
      }
    }

  /**
   * Combines the [elements] by grouping the elements from the left and
   * calling [callback] on all consecutive elements with the corresponding separator.
   */
  fun foldLeft(callback: (R, S, R) -> R): R {
    var result = elements.first()
    for (i in 1 until elements.size) {
      result = callback(result, separators[i - 1], elements[i])
    }
    return result
  }

  /**
   * Combines the [elements] by grouping the elements from the right and
   * calling [callback] on all consecutive elements with the corresponding separator.
   */
  fun foldRight(callback: (R, S, R) -> R): R {
    var result = elements.last()
    for (i in elements.size - 2 downTo 0) {
      result = callback(elements[i], separators[i], result)
    }
    return result
  }
}

/** Returns a parser that accepts the receiver zero or more times, separated by [separator]. */
fun <R, S> Parser<R>.starSeparated(separator: Parser<S>): SeparatedParser<R, S> =
  repeatSeparated(separator, min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times, separated by [separator]. */
fun <R, S> Parser<R>.plusSeparated(separator: Parser<S>): SeparatedParser<R, S> =
  repeatSeparated(separator, min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times, separated by [separator]. */
fun <R, S> Parser<R>.timesSeparated(separator: Parser<S>, count: Int): SeparatedParser<R, S> =
  repeatSeparated(separator, min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times, separated by [separator]. */
fun <R, S> Parser<R>.repeatSeparated(
  separator: Parser<S>,
  min: Int,
  max: Int = min,
): SeparatedParser<R, S> = SeparatedParser(this, separator, min, max)

/** Alias for [SeparatedParser] for parity with canonical PetitParser. */
typealias SeparatedRepeatingParser<R, S> = SeparatedParser<R, S>

/**
 * A parser that repeatedly parses between [min] and [max] instances of its delegate
 * separated by [separator].
 */
class SeparatedParser<R, S>(
  delegate: Parser<R>,
  var separator: Parser<S>,
  min: Int,
  max: Int = min,
) : RepeatingParser<R, SeparatedList<R, S>>(delegate, min, max), SequentialParser {
  override val children: List<Parser<*>>
    get() = listOf(delegate, separator)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (separator == source) {
      separator = target as Parser<S>
    }
  }

  override fun parseOn(input: Input): Output<SeparatedList<R, S>> {
    var current = input
    val elements = mutableListOf<R>()
    val separators = mutableListOf<S>()
    while (elements.size < min) {
      if (elements.isNotEmpty()) {
        when (val separation = separator.parseOn(current)) {
          is Output.Failure -> return separation
          is Output.Success -> {
            current = separation
            separators.add(separation.value)
          }
        }
      }
      when (val result = delegate.parseOn(current)) {
        is Output.Failure -> return result
        is Output.Success -> {
          current = result
          elements.add(result.value)
        }
      }
    }
    while (elements.size < max) {
      val previous = current
      if (elements.isNotEmpty()) {
        when (val separation = separator.parseOn(current)) {
          is Output.Failure -> break
          is Output.Success -> {
            current = separation
            separators.add(separation.value)
          }
        }
      }
      when (val result = delegate.parseOn(current)) {
        is Output.Failure -> {
          if (elements.isNotEmpty()) separators.removeLast()
          return previous.success(SeparatedList(elements, separators))
        }
        is Output.Success -> {
          current = result
          elements.add(result.value)
        }
      }
    }
    return current.success(SeparatedList(elements, separators))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    var count = 0
    var current = position
    while (count < min) {
      if (count > 0) {
        val separation = separator.fastParseOn(buffer, current)
        if (separation < 0) return -1
        current = separation
      }
      val result = delegate.fastParseOn(buffer, current)
      if (result < 0) return -1
      count++
      current = result
    }
    while (count < max) {
      val previous = current
      if (count > 0) {
        val separation = separator.fastParseOn(buffer, current)
        if (separation < 0) break
        current = separation
      }
      val result = delegate.fastParseOn(buffer, current)
      if (result < 0) return previous
      count++
      current = result
    }
    return current
  }

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is SeparatedParser<*, *>

  override fun copy(): SeparatedParser<R, S> =
    SeparatedParser(delegate, separator, min, max)
}
