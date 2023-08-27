package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the [string]. */
fun string(string: String, message: String = "'$string' expected", ignoreCase: Boolean = false) =
  string({ string.equals(it, ignoreCase) }, string.length, message)

/** Returns a parser that accepts string satisfying the given [predicate]. */
fun string(predicate: (String) -> Boolean, length: Int, message: String) = object : Parser<String> {
  override fun parseOn(input: Input): Output<String> {
    val stop = input.position + length
    if (stop <= input.buffer.length) {
      val string = input.buffer.substring(input.position, stop)
      if (predicate(string)) {
        return input.success(string, stop)
      }
    }
    return input.failure(message)
  }
}
