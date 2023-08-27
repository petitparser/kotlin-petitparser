package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts any character. */
fun any(message: String = "input expected") = char(CharPredicate.any(), message)

/** Returns a parser that accepts any of the provided [chars]. */
fun anyOf(chars: String, message: String = "any of [$chars] expected") =
  char(CharPredicate.anyOf(chars), message)

/** Returns a parser that accepts none of the provided [chars]. */
fun noneOf(chars: String, message: String = "none of [$chars] expected") =
  char(CharPredicate.noneOf(chars), message)

/** Returns a parser that accepts the provided [pattern]. */
fun pattern(pattern: String, message: String = "[$pattern] expected") =
  char(CharPredicate.pattern(pattern), message)

/** Returns a parser that accepts a digit character. */
fun digit(message: String = "digit expected") = char(Char::isDigit, message)

/** Returns a parser that accepts a letter character. */
fun letter(message: String = "letter expected") = char(Char::isLetter, message)

/** Returns a parser that accepts a letter or digit character. */
fun letterOrDigit(message: String = "letter or digit expected") =
  char(Char::isLetterOrDigit, message)

/** Returns a parser that accepts a whitespace character. */
fun whitespace(message: String = "whitespace expected") = char(Char::isWhitespace, message)

/** Returns a parser that accepts a specified [char]. */
fun char(char: Char, message: String = "'$char' expected") = char(CharPredicate.char(char), message)

/** Returns a parser that accepts a specified character [category]. */
fun char(category: CharCategory, message: String = "$category expected") =
  char(category::contains, message)

/** Returns a parser that accepts a character satisfying a [predicate]. */
fun char(predicate: CharPredicate, message: String) = object : Parser<Char> {
  override fun parseOn(input: Input): Output<Char> {
    if (input.position < input.buffer.length) {
      val char = input.buffer[input.position]
      if (predicate.test(char)) {
        return input.success(char, input.position + 1)
      }
    }
    return input.failure(message)
  }
}
