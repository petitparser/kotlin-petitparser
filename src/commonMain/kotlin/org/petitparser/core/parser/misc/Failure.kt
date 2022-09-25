package org.petitparser.core.parser.misc

import org.petitparser.core.context.Context
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and fails with a [message]. */
fun <R> failure(message: String = "unable to read") = object : Parser<R> {
  override fun parseOn(context: Context) = context.failure<R>(message)
}