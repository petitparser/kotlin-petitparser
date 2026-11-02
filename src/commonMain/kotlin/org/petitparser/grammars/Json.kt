package org.petitparser.grammars

import org.petitparser.core.definition.GrammarDefinition
import org.petitparser.core.definition.ref
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.combinator.followedBy
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.surroundedBy
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

class JsonGrammar : GrammarDefinition<Any?>() {

  // JSON atoms
  val trueToken: Parser<Boolean> by def { token("true").map { true } }
  val falseToken: Parser<Boolean> by def { token("false").map { false } }
  val nullToken: Parser<Nothing?> by def { token("null").map { null } }
  val numberToken: Parser<Double> by def {
    seq(
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
  }

  val characterNormal: Parser<Char> by def { pattern("^\"\\") }
  val characterEscape: Parser<Char> by def {
    seqMap(
      char('\\'), anyOf(ESCAPE_CHARS.keys.joinToString("")),
    ) { _, value -> ESCAPE_CHARS.getValue(value) }
  }
  val characterUnicode: Parser<Char> by def {
    seqMap(
      string("\\u"), pattern("0-9A-Fa-f").times(4).flatten(),
    ) { _, value -> value.toInt(radix = 16).toChar() }
  }
  val characterPrimitive: Parser<Char> by def {
    or(
      ref(::characterNormal),
      ref(::characterEscape),
      ref(::characterUnicode),
    )
  }
  val stringToken: Parser<String> by def {
    ref(::characterPrimitive).star()
      .map { chars -> chars.joinToString("") }
      .surroundedBy(char('"'))
      .trim()
  }

  // Composite elements
  val entry: Parser<Pair<String, Any?>> by def {
    seqMap(
      ref(::stringToken),
      token(':'),
      ref(::jsonValue),
    ) { key, _, value -> Pair(key, value) }
  }
  val entries: Parser<List<Pair<String, Any?>>> by def {
    ref(::entry).starSeparated(token(',')).map { it.elements }
  }
  val jsonObject: Parser<Map<String, Any?>> by def {
    ref(::entries)
      .followedBy(token(',').optional())
      .surroundedBy(token('{'), token('}'))
      .map { it.toMap() }
  }

  val member: Parser<Any?> by def { ref(::jsonValue) }
  val members: Parser<List<Any?>> by def {
    ref(::member).starSeparated(token(',')).map { it.elements }
  }
  val jsonArray: Parser<List<Any?>> by def {
    ref(::members)
      .followedBy(token(',').optional())
      .surroundedBy(token('['), token(']'))
  }

  val jsonValue: Parser<Any?> by def {
    or(
      ref(::jsonObject),
      ref(::jsonArray),
      ref(::stringToken),
      ref(::numberToken),
      ref(::trueToken),
      ref(::falseToken),
      ref(::nullToken),
    )
  }

  val start: Parser<Any?> by def { ref(::jsonValue).end() }

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
