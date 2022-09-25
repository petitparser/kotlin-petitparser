package org.petitparser.core.parser.misc

import org.petitparser.core.context.Context
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and produces the current position in the input. */
fun position() = object : Parser<Int> {
  override fun parseOn(context: Context) = context.success(context.position)
}