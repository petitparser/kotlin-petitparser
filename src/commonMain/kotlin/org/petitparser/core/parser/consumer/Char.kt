package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts any character. */
fun any(message: String = "input expected") = char({ true }, message)

/** Returns a parser that accepts a specified [char]. */
fun char(char: Char, message: String = "'$char' expected") = char(char::equals, message)

/** Returns a parser that accepts a specified character [category]. */
fun char(category: CharCategory, message: String = "$category expected") =
  char(category::contains, message)

/** Returns a parser that accepts a digit character. */
fun digit(message: String = "digit expected") = char(Char::isDigit, message)

/** Returns a parser that accepts a letter character. */
fun letter(message: String = "letter expected") = char(Char::isLetter, message)

/** Returns a parser that accepts a whitespace character. */
fun whitespace(message: String = "whitespace expected") = char(Char::isWhitespace, message)

/** Returns a parser that accepts a character satisfying a [predicate]. */
fun char(predicate: (Char) -> Boolean, message: String) = object : Parser<Char> {
  override fun parseOn(context: Context): Result<Char> {
    if (context.position < context.buffer.length) {
      val char = context.buffer[context.position]
      if (predicate(char)) {
        return context.success(char, context.position + 1)
      }
    }
    return context.failure(message)
  }
}
