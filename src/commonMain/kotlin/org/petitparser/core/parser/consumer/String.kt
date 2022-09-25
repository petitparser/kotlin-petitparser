package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts the [string]. */
fun string(string: String, message: String = "'$string' expected", ignoreCase: Boolean = false) =
  string({ string.equals(it, ignoreCase) }, string.length, message)

/** Returns a parser that accepts string satisfying the given [predicate]. */
fun string(predicate: (String) -> Boolean, length: Int, message: String) = object : Parser<String> {
  override fun parseOn(context: Context): Result<String> {
    val stop = context.position + length
    if (stop <= context.buffer.length) {
      val string = context.buffer.substring(context.position, stop)
      if (predicate(string)) {
        return context.success(string, stop)
      }
    }
    return context.failure(message)
  }
}
