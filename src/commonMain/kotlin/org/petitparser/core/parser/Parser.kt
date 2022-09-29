package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output

interface Parser<out R> {
  fun parse(input: String, position: Int = 0): Output<R> = parseOn(Input.InputImpl(input, position))

  fun parseOn(input: Input): Output<R>

  /** A list of child parsers. */
  val children: List<Parser<*>> get() = emptyList()
}

fun <R> Parser<R>.accept(input: String, start: Int = 0) = when (parse(input, start)) {
  is Output.Success -> true
  is Output.Failure -> false
}

