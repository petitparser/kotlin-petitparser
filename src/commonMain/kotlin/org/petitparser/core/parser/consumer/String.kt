package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the [string]. */
fun string(
  string: String,
  message: String = "'$string' expected",
  ignoreCase: Boolean = false,
): StringParser = StringParser(string, message, ignoreCase)

/** Returns a parser that accepts this [String]. */
fun String.toParser(
  message: String = "'$this' expected",
  ignoreCase: Boolean = false,
): StringParser = string(this, message, ignoreCase)

/** Returns a parser that accepts a string satisfying the given [predicate]. */
fun string(
  predicate: (String) -> Boolean,
  length: Int,
  message: String,
): PredicateStringParser = PredicateStringParser(predicate, length, message)

/** A parser for a literal string. */
class StringParser(
  val literal: String,
  val message: String = "'$literal' expected",
  val ignoreCase: Boolean = false,
) : Parser<String> {
  override fun parseOn(input: Input): Output<String> {
    val position = input.position
    val stop = position + literal.length
    val buffer = input.buffer
    if (position >= 0 && stop <= buffer.length) {
      if (buffer.regionMatches(position, literal, 0, literal.length, ignoreCase = ignoreCase)) {
        return input.success(if (ignoreCase) buffer.substring(position, stop) else literal, stop)
      }
    }
    return input.failure(message)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val stop = position + literal.length
    return if (position >= 0 &&
      stop <= buffer.length &&
      buffer.regionMatches(position, literal, 0, literal.length, ignoreCase = ignoreCase)
    ) {
      stop
    } else {
      -1
    }
  }

  override fun copy(): StringParser = StringParser(literal, message, ignoreCase)

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is StringParser &&
      literal == other.literal &&
      message == other.message &&
      ignoreCase == other.ignoreCase
}

/** A parser for a string of a fixed [length] satisfying a [predicate]. */
class PredicateStringParser(
  val predicate: (String) -> Boolean,
  val length: Int,
  val message: String,
) : Parser<String> {
  override fun parseOn(input: Input): Output<String> {
    val position = input.position
    val stop = position + length
    val buffer = input.buffer
    if (position >= 0 && stop <= buffer.length) {
      val str = buffer.substring(position, stop)
      if (predicate(str)) {
        return input.success(str, stop)
      }
    }
    return input.failure(message)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val stop = position + length
    return if (position >= 0 && stop <= buffer.length && predicate(buffer.substring(position, stop))) {
      stop
    } else {
      -1
    }
  }

  override fun copy(): PredicateStringParser = PredicateStringParser(predicate, length, message)

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is PredicateStringParser &&
      predicate == other.predicate &&
      length == other.length &&
      message == other.message
}
