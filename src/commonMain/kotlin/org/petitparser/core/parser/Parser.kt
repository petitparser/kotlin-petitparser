package org.petitparser.core.parser

import org.petitparser.core.context.Context
import org.petitparser.core.context.ContextImpl
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success

interface Parser<out R> {

  fun parse(input: String, position: Int = 0): Result<R> = parseOn(ContextImpl(input, position))

  fun parseOn(context: Context): Result<R>
}

fun <R> Parser<R>.accept(input: String, start: Int = 0) =
  parse(input, start) is Success<*>

