package org.petitparser.core.parser.utils

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.combinator.seq2
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.seqOf
import org.petitparser.core.parser.combinator.skip
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.repeater.starSeparated
import kotlin.test.Test
import kotlin.test.assertTrue

class SequentialParserTest {
  @Test
  fun test_sequenceList() {
    val parser: Parser<*> = seqOf(char('a'), char('b'))
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_sequencePlus() {
    val parser: Parser<*> = char('a') + char('b')
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_sequenceTuple() {
    val parser: Parser<*> = seq2(char('a'), char('b'))
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_sequenceMap() {
    val parser: Parser<*> = seqMap(char('a'), char('b')) { a, b -> "$a$b" }
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_skip() {
    val parser: Parser<*> = char('a').skip(before = char('b'))
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_separated() {
    val parser: Parser<*> = char('a').starSeparated(char(','))
    assertTrue(parser is SequentialParser)
  }

  @Test
  fun test_trim() {
    val parser: Parser<*> = char('a').trim()
    assertTrue(parser is SequentialParser)
  }
}
