package org.petitparser.grammar.lisp

import org.petitparser.core.grammar.Grammar
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.*
import org.petitparser.core.parser.consumer.CharPredicate.Companion.any
import org.petitparser.core.parser.consumer.CharPredicate.Companion.anyOf
import org.petitparser.core.parser.consumer.CharPredicate.Companion.char
import org.petitparser.core.parser.consumer.CharPredicate.Companion.pattern
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.whitespace
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.star

class LispGrammar : Grammar() {

  private val comment by seq(char(';'), newline().not().star())
  private val space by or(whitespace(), ref(comment))

  private val splice by seq(char('@'), list)
  private val unquote by seq(char(','), ref(list))
  private val quasiquote by seq(char('`'), ref(list))
  private val quote by seq(char('\''), ref(atom))

  private val symbolToken by seqMap(
    pattern("a-zA-Z!#\$%&*/:<=>?@\\^_|~+-"),
    pattern("a-zA-Z0-9!#\$%&*/:<=>?@\\^_|~+-")).star()
  private val symbol = symbolToken.flatten("Symbol expected")

  private val characterRaw = pattern("^\"")
  private val characterEscape by seqMap(char('\\'), any())
  private val character by or(ref(characterEscape), ref(characterRaw))
  private val `string` by seq(
    char('"'),
    character.star(),
    char('"')) { _, it, _ -> it}


  private val empty = ref(space).star()
  private val cell by seq(atom, cells)
  private val cells by or(ref(cell), ref(empty))

  private val numberToken by seqMap(
  ref(anyOf("-+")).optional(),
  char('0') or digit().plus(),
  char('.').seq(digit().plus()).optional() &
  anyOf('eE').seq(anyOf('-+').optional()).seq(digit().plus()).optional()
  )
  private val number = ref(numberToken).flatten("Number expected")

  private val list by seqMap(
    char('('),
    cell,
    char(')'),
  ) { _, it, _ -> it.elements }


  private val atomChoice: Parser<Any?> by or(
    list,
    number,
    string,
    symbol,
    quote,
    quasiquote,
    unquote,
    splice
  )

  val start by atomChoice.star().end()
}