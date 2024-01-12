package org.petitparser.grammar.lisp

import org.petitparser.core.grammar.Grammar
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.combinator.not
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.anyOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.consumer.whitespace
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star

class LispGrammar : Grammar() {

  private val comment by seq(char(';'), newline().not().star())
  private val space by or(whitespace(), comment)

  private val splice by seq(char('@'), ref(::list))
  private val unquote by seq(char(','), ref(::list))
  private val quasiquote by seq(char('`'), ref(::list))
  private val quote by seq(char('\''), ref(::atom))

  private val symbolToken by seq(
    pattern("a-zA-Z!#\$%&*/:<=>?@\\^_|~+-"),
    pattern("a-zA-Z0-9!#\$%&*/:<=>?@\\^_|~+-"),
  ).star()
  private val symbol by symbolToken.flatten()

  private val characterRaw by pattern("^\"")
  private val characterEscape by seq(char('\\'), any())
  private val character by or(characterEscape, characterRaw)
  private val `string` by seq(
    char('"'),
    character.star(),
    char('"'),
    )

  private val empty by space.star()
  private val cell by seq(ref(::atom), ref(::cells))
  private val cells: Parser<Any?> by or(cell, empty)

  private val numberToken by seq(
    anyOf("-+").optional(),
    char('0') or digit().plus(),
    char('.').seq(digit().plus()).optional(),
    anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional(),
  )
  private val number by numberToken.flatten()

  private val list by seq(
    char('('),
    cell,
    char(')')
  )

  private val atom: Parser<Any?> by or(
    list,
    number,
    string,
    symbol,
    quote,
    quasiquote,
    unquote,
    splice
  )

  val start by atom.star().end()
}
