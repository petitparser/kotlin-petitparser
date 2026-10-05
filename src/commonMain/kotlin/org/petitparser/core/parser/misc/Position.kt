package org.petitparser.core.parser.misc

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that reports the current input position. */
fun position(): PositionParser = PositionParser()

/** A parser that reports the current input position. */
class PositionParser : Parser<Int> {
  override fun parseOn(input: Input): Output<Int> = input.success(input.position)

  override fun fastParseOn(buffer: String, position: Int): Int = position

  override fun toString(): String = "${this::class.simpleName}"

  override fun copy(): PositionParser = PositionParser()
}