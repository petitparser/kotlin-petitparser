package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and fails with a [message]. */
fun <R> failure(message: String = "unable to parse"): FailureParser<R> =
  FailureParser(message)

/** A parser that consumes nothing and fails. */
class FailureParser<out R>(
  val message: String = "unable to parse",
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> = input.failure(message)

  override fun fastParseOn(buffer: String, position: Int): Int = -1

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun copy(): FailureParser<R> = FailureParser(message)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is FailureParser<*> && message == other.message
}