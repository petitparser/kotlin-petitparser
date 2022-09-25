package org.petitparser.core.parser.action

import org.petitparser.core.context.Context
import org.petitparser.core.context.Success
import org.petitparser.core.parser.Parser

/** Returns a parser that extracts the element at the provided [index]. */
fun <R> Parser<List<R>>.pick(index: Int) = object : Parser<R> {
  override fun parseOn(context: Context) = when (val result = this@pick.parseOn(context)) {
    is Success -> result.success(result.value[index])
    else -> result.failure(result.message)
  }
}

