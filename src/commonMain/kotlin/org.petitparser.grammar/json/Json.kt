package org.petitparser.grammar.json

import org.petitparser.core.grammar.Grammar
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.consumer.anyOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.starSeparated
import org.petitparser.core.parser.repeater.times

class JsonGrammar : Grammar() {

  // JSON atoms
  private val trueToken by token("true").map { true }
  private val falseToken by token("false").map { false }
  private val nullToken by token("null").map { null }
  private val numberToken by seq(
    char('-').optional(),
    char('0') or digit().plus(),
    seq(
      char('.'),
      digit().plus(),
    ).optional(),
    seq(
      anyOf("eE"),
      anyOf("+-").optional(),
      digit().plus(),
    ).optional(),
  ).flatten().trim().map(String::toDouble)

  private val characterNormal by pattern("^\"\\")
  private val characterEscape by seqMap(
    char('\\'), anyOf(ESCAPE_CHARS.keys.joinToString(""))
  ) { _, value -> ESCAPE_CHARS[value]!! }
  private val characterUnicode by seqMap(
    string("\\u"), pattern("0-9A-Fa-f").times(4).flatten()
  ) { _, value -> value.toInt(radix = 16).toChar() }
  private val characterPrimitive by or(
    characterNormal,
    characterEscape,
    characterUnicode,
  )
  private val stringToken by seqMap(
    char('"'),
    characterPrimitive.star(),
    char('"'),
  ) { _, chars, _ -> chars.joinToString("") }.trim()

  // Composite elements
  private val entry by seqMap(
    stringToken,
    token(':'),
    ref(::jsonValue),
  ) { key, _, value -> Pair(key, value) }
  private val entries by entry.starSeparated(token(','))
  private val jsonObject by seqMap(
    token('{'),
    entries,
    token(',').optional(),
    token('}'),
  ) { _, it, _, _ -> it.toMap() }

  private val member by ref(::jsonValue)
  private val members by member.starSeparated(token(','))
  private val jsonArray by seqMap(
    token('['),
    members,
    token(',').optional(),
    token(']'),
  ) { _, it, _, _ -> it }

  private val jsonValue: Parser<Any?> by or(
    jsonObject,
    jsonArray,
    stringToken,
    numberToken,
    trueToken,
    falseToken,
    nullToken,
  )
  val start by jsonValue.end()

  private fun token(value: Char) = char(value).trim()
  private fun token(value: String) = string(value).trim()
}

val ESCAPE_CHARS = buildMap {
  put('\\', '\\')
  put('/', '/')
  put('"', '"')
  put('b', '\b')
  put('f', '\u000C')
  put('n', '\n')
  put('r', '\r')
  put('t', '\t')
}
