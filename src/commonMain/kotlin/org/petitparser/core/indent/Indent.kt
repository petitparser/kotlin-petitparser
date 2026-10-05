package org.petitparser.core.indent

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.where
import org.petitparser.core.parser.combinator.and
import org.petitparser.core.parser.combinator.skip
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.misc.epsilon
import org.petitparser.core.parser.misc.failure
import org.petitparser.core.parser.repeater.plusString
import org.petitparser.core.parser.repeater.starString
import org.petitparser.core.parser.utils.selectFirst

/**
 * Stateful set of parsers to handle indentation-based grammars.
 */
class Indent(
  val parser: Parser<Char> = pattern(" \t"),
  val message: String = "indented expected",
) {
  val stack = mutableListOf<String>()
  var current: String = ""

  /** Parser that increases the indentation. */
  val increase: Parser<String> by lazy {
    parser
      .plusString(message = message)
      .where(message = message) { value ->
        if (value.startsWith(current) && value.length > current.length) {
          stack.add(current)
          current = value
          true
        } else {
          false
        }
      }
      .and()
  }

  /** Parser that consumes and matches the current indentation level. */
  val same: Parser<String> by lazy {
    parser
      .starString(message = message)
      .where(message = message) { value -> value == current }
  }

  /** Parser that decreases the indentation by one level. */
  val decrease: Parser<Unit> by lazy {
    epsilon(Unit).where(message = message) {
      if (stack.isNotEmpty()) {
        current = stack.removeLast()
        true
      } else {
        false
      }
    }
  }

  /**
   * Runs [parser] in a deeper indentation scope with automatic state rollback on failure.
   */
  fun <R> during(parser: Parser<R>): Parser<R> =
    listOf(parser, failure<R>().skip(before = decrease))
      .toChoiceParser(failureJoiner = ::selectFirst)
      .skip(before = increase, after = decrease)
}
