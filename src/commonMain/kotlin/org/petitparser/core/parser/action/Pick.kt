package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Returns a parser that extracts the element at the provided [index]. */
fun <R> Parser<List<R>>.pick(index: Int) = object : Parser<R> {
  override val children = listOf(this@pick)
  override fun parseOn(input: Input) = when (val result = this@pick.parseOn(input)) {
    is Output.Success -> result.success(result.value[index])
    is Output.Failure -> result
  }
}

