package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceListTest {
  @Test
  fun test_sequence() {
    val parser = seqOf(char('a'), char('b'), char('c'))
    expectParserInvariants(parser)
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }

  @Test
  fun test_sequence_operator() {
    val parser = char('a') seq char('b') seq char('c')
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }

  @Test
  fun test_sequence_plus() {
    val parser = char('a') + char('b') + char('c')
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }

  @Test
  fun test_sequence_toSequenceParser() {
    val parser = listOf(char('a'), char('b'), char('c')).toSequenceParser()
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
  }

  @Test
  fun test_sequence_single() {
    val parser = seqOf(char('a'))
    expectParserInvariants(parser)
    assertEquals(1, parser.children.size)
    assertSuccess(parser, "a", listOf('a'))
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "b", "'a' expected", 0)
  }

  @Test
  fun test_sequence_empty() {
    val parser = seqOf<Char>()
    expectParserInvariants(parser)
    assertEquals(0, parser.children.size)
    assertSuccess(parser, "", emptyList())
    assertSuccess(parser, "abc", emptyList(), 0)
  }
}
