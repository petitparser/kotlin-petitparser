package org.petitparser.core.parser.action

import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser returns the result of [handler] on success of the receiver. */
fun <T, R> Parser<T>.map(handler: (T) -> R) = Parser { input ->
  when (val result = this@map.parseOn(input)) {
    is Output.Success -> result.success(handler(result.value))
    is Output.Failure -> result
  }
}

/** Returns a parser that extracts the element at the provided [index]. */
fun <R> Parser<List<R>>.pick(index: Int) = map { value -> value[index] }

/** Returns a parser that extracts elements at the specified [indices] range. */
fun <R> Parser<List<R>>.slice(indices: IntRange) = map { value -> value.slice(indices) }

/** Returns a parser that extracts elements at specified [indices]. */
fun <R> Parser<List<R>>.slice(indices: Iterable<Int>) = map { value -> value.slice(indices) }
