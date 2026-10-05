package org.petitparser.core.parser.consumer

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that accepts any character. */
fun any(message: String = "input expected"): CharacterParser =
  char(CharPredicate.any(), message)

/** Returns a parser that accepts any Unicode character (including surrogate pairs). */
fun anyUnicode(message: String = "input expected"): UnicodeCharacterParser =
  unicodeChar(CharPredicate.any(), message)

/** Returns a parser that accepts any of the provided [chars]. */
fun anyOf(
  chars: String,
  message: String = "any of [$chars] expected",
  ignoreCase: Boolean = false,
): CharacterParser =
  char(CharPredicate.anyOf(chars, ignoreCase = ignoreCase), message)

/** Returns a parser that accepts any of the provided Unicode [chars]. */
fun anyOfUnicode(
  chars: String,
  message: String = "any of [$chars] expected",
  ignoreCase: Boolean = false,
): UnicodeCharacterParser =
  unicodeChar(CharPredicate.anyOf(chars, ignoreCase = ignoreCase, unicode = true), message)

/** Returns a parser that accepts any char of the provided Unicode [category]. */
fun category(category: CharCategory, message: String = "$category expected"): CharacterParser =
  char(category::contains, message)

/** Returns a parser that accepts none of the provided [chars]. */
fun noneOf(
  chars: String,
  message: String = "none of [$chars] expected",
  ignoreCase: Boolean = false,
): CharacterParser =
  char(CharPredicate.noneOf(chars, ignoreCase = ignoreCase), message)

/** Returns a parser that accepts none of the provided Unicode [chars]. */
fun noneOfUnicode(
  chars: String,
  message: String = "none of [$chars] expected",
  ignoreCase: Boolean = false,
): UnicodeCharacterParser =
  unicodeChar(CharPredicate.noneOf(chars, ignoreCase = ignoreCase, unicode = true), message)

/** Returns a parser that accepts the provided [pattern]. */
fun pattern(
  pattern: String,
  message: String = "[$pattern] expected",
  ignoreCase: Boolean = false,
): CharacterParser =
  char(CharPredicate.pattern(pattern, ignoreCase = ignoreCase), message)

/** Returns a parser that accepts the provided Unicode [pattern]. */
fun patternUnicode(
  pattern: String,
  message: String = "[$pattern] expected",
  ignoreCase: Boolean = false,
): UnicodeCharacterParser =
  unicodeChar(CharPredicate.pattern(pattern, ignoreCase = ignoreCase, unicode = true), message)

/** Returns a parser that accepts a digit character. */
fun digit(message: String = "digit expected"): CharacterParser =
  char(CharPredicate.digit(), message)

/** Returns a parser that accepts a letter character. */
fun letter(message: String = "letter expected"): CharacterParser =
  char(CharPredicate.letter(), message)

/** Returns a parser that accepts a letter or digit character. */
fun letterOrDigit(message: String = "letter or digit expected"): CharacterParser =
  char(Char::isLetterOrDigit, message)

/** Returns a parser that accepts a word character (`[a-zA-Z0-9_]`). */
fun word(message: String = "letter or digit expected"): CharacterParser =
  char(CharPredicate.word(), message)

/** Returns a parser that accepts a lowercase character. */
fun lowercase(message: String = "lowercase letter expected"): CharacterParser =
  char(CharPredicate.lowercase(), message)

/** Returns a parser that accepts an uppercase character. */
fun uppercase(message: String = "uppercase letter expected"): CharacterParser =
  char(CharPredicate.uppercase(), message)

/** Returns a parser that accepts a whitespace character. */
fun whitespace(message: String = "whitespace expected"): CharacterParser =
  char(CharPredicate.whitespace(), message)

/** Returns a parser that accepts a character between [start] and [stop]. */
fun range(
  start: Char,
  stop: Char,
  message: String = "[$start-$stop] expected",
): CharacterParser =
  char(CharPredicate.range(start, stop), message)

/** Returns a parser that accepts a character in [range]. */
fun range(
  range: CharRange,
  message: String = "[${range.first}-${range.last}] expected",
): CharacterParser =
  char(CharPredicate.range(range), message)

/** Returns a parser that accepts a specified [char]. */
fun char(
  char: Char,
  message: String = "'$char' expected",
  ignoreCase: Boolean = false,
): CharacterParser =
  if (ignoreCase) {
    anyOf("${char.lowercase()}${char.uppercaseChar()}", message)
  } else {
    char(CharPredicate.char(char), message)
  }

/** Returns a parser that accepts this [Char]. */
fun Char.toParser(message: String = "'$this' expected", ignoreCase: Boolean = false): CharacterParser =
  char(this, message, ignoreCase)

/** Returns a parser that accepts a specified character [category]. */
fun char(category: CharCategory, message: String = "$category expected"): CharacterParser =
  char(category::contains, message)

/** Returns a parser that accepts a character satisfying a [predicate]. */
fun char(predicate: CharPredicate, message: String): CharacterParser =
  CharacterParser(predicate, message)

/** Returns a parser that accepts a Unicode character satisfying a [predicate]. */
fun unicodeChar(predicate: CharPredicate, message: String): UnicodeCharacterParser =
  UnicodeCharacterParser(predicate, message)

/** Returns a parser that accepts a specific Unicode code point [code]. */
fun unicodeChar(
  code: Int,
  message: String = "'${codePointToString(code)}' expected",
): UnicodeCharacterParser =
  UnicodeCharacterParser(CharPredicate.char(code), message)

/** Returns a parser that accepts a specific Unicode character [char]. */
fun unicodeChar(
  char: String,
  message: String = "'$char' expected",
): UnicodeCharacterParser =
  UnicodeCharacterParser(CharPredicate.anyOf(char, unicode = true), message)

/**
 * Parser for an individual character satisfying a [predicate].
 */
class CharacterParser(
  val predicate: CharPredicate,
  val message: String,
) : Parser<Char> {
  override fun parseOn(input: Input): Output<Char> {
    val position = input.position
    val buffer = input.buffer
    if (position < buffer.length) {
      val char = buffer[position]
      if (predicate.test(char)) {
        return input.success(char, position + 1)
      }
    }
    return input.failure(message)
  }

  override fun fastParseOn(buffer: String, position: Int): Int =
    if (position < buffer.length && predicate.test(buffer[position])) {
      position + 1
    } else {
      -1
    }

  override fun copy(): CharacterParser = CharacterParser(predicate, message)

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is CharacterParser &&
      predicate == other.predicate &&
      message == other.message
}

typealias CharParser = CharacterParser

/**
 * Parser for a Unicode character (including UTF-16 surrogate pairs) satisfying a [predicate].
 */
class UnicodeCharacterParser(
  val predicate: CharPredicate,
  val message: String,
) : Parser<String> {
  override fun parseOn(input: Input): Output<String> {
    val buffer = input.buffer
    val position = input.position
    if (position < buffer.length) {
      val char1 = buffer[position]
      var code = char1.code
      var nextPosition = position + 1
      if (char1.isHighSurrogate() && nextPosition < buffer.length) {
        val char2 = buffer[nextPosition]
        if (char2.isLowSurrogate()) {
          code = 0x10000 + ((char1.code and 0x3FF) shl 10) + (char2.code and 0x3FF)
          nextPosition++
        }
      }
      if (predicate.test(code)) {
        return input.success(buffer.substring(position, nextPosition), nextPosition)
      }
    }
    return input.failure(message)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val length = buffer.length
    if (position < length) {
      val char1 = buffer[position]
      var code = char1.code
      var nextPosition = position + 1
      if (char1.isHighSurrogate() && nextPosition < length) {
        val char2 = buffer[nextPosition]
        if (char2.isLowSurrogate()) {
          code = 0x10000 + ((char1.code and 0x3FF) shl 10) + (char2.code and 0x3FF)
          nextPosition++
        }
      }
      if (predicate.test(code)) {
        return nextPosition
      }
    }
    return -1
  }

  override fun copy(): UnicodeCharacterParser = UnicodeCharacterParser(predicate, message)

  override fun toString(): String = "${this::class.simpleName}[$message]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is UnicodeCharacterParser &&
      predicate == other.predicate &&
      message == other.message
}
