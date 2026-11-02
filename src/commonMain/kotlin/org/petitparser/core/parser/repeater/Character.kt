package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.consumer.CharParser
import org.petitparser.core.parser.consumer.CharPredicate

/** Returns a parser that accepts the receiver zero or more times. */
fun Parser<*>.starString(message: String? = null): Parser<String> =
  repeatString(min = 0, max = Int.MAX_VALUE, message = message)

/** Returns a parser that accepts the receiver one or more times. */
fun Parser<*>.plusString(message: String? = null): Parser<String> =
  repeatString(min = 1, max = Int.MAX_VALUE, message = message)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun Parser<*>.timesString(count: Int, message: String? = null): Parser<String> =
  repeatString(min = count, max = count, message = message)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun Parser<*>.repeatString(
  min: Int,
  max: Int = min,
  message: String? = null,
): Parser<String> = when (this) {
  is CharParser -> RepeatingCharacterParser(predicate, message ?: this.message, min, max)
  else -> this.repeat(min, max).flatten(message)
}

/**
 * A parser that repeatedly parses between [min] and [max] characters matching [predicate].
 */
class RepeatingCharacterParser(
  val predicate: CharPredicate,
  val message: String,
  val min: Int,
  val max: Int = min,
) : Parser<String> {
  init {
    require(min >= 0) { "min must be at least 0, but got $min" }
    require(min <= max) { "max must be at least $min, but got $max" }
  }

  override fun parseOn(input: Input): Output<String> {
    val buffer = input.buffer
    val start = input.position
    val end = buffer.length
    var position = start
    var count = 0
    while (count < max && position < end && predicate.test(buffer[position])) {
      position++
      count++
    }
    return if (count >= min) {
      input.success(buffer.substring(start, position), position)
    } else {
      input.failure(message, position)
    }
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val end = buffer.length
    var current = position
    var count = 0
    while (count < max && current < end && predicate.test(buffer[current])) {
      current++
      count++
    }
    return if (count >= min) current else -1
  }

  override fun copy(): RepeatingCharacterParser =
    RepeatingCharacterParser(predicate, message, min, max)

  override fun toString(): String =
    "${this::class.simpleName}[$message, $min..${if (max == Int.MAX_VALUE) "*" else max}]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is RepeatingCharacterParser &&
      predicate == other.predicate &&
      message == other.message &&
      min == other.min &&
      max == other.max
}
