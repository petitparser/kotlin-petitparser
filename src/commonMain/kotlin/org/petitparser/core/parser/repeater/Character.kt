package org.petitparser.core.parser.repeater

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.consumer.CharParser

/** Returns a parser that accepts the receiver zero or more times. */
fun Parser<Char>.starString() = repeatString(min = 0, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver one or more times. */
fun Parser<Char>.plusString() = repeatString(min = 1, max = Int.MAX_VALUE)

/** Returns a parser that accepts the receiver exactly [count] times. */
fun Parser<Char>.timesString(count: Int) = repeatString(min = count, max = count)

/** Returns a parser that accepts the receiver between [min] and [max] times. */
fun Parser<Char>.repeatString(min: Int, max: Int): Parser<String> = when (this) {
  is CharParser -> object : Parser<String> {
    val message = this@repeatString.message
    val predicate = this@repeatString.predicate
    override fun parseOn(input: Input): Output<String> {
      val buffer = input.buffer
      val start = input.position
      val end = input.buffer.length
      var position = start
      var count = 0
      while (count < min) {
        if (position >= end || !predicate.test(buffer[position])) {
          return input.failure(message, position)
        }
        position++
        count++
      }
      while (position < end && count < max) {
        if (!predicate.test(buffer[position])) {
          break
        }
        position++
        count++
      }
      return input.success(buffer.substring(start, position), position)
    }
  }
  else -> this.repeat(min, max).flatten()
}

