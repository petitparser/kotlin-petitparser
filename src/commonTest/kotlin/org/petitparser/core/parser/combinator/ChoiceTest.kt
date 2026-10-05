package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.anyOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.utils.selectFarthest
import org.petitparser.core.parser.utils.selectFarthestJoined
import org.petitparser.core.parser.utils.selectFirst
import org.petitparser.core.parser.utils.selectLast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChoiceTest {
  private val failureA0 = Output.Failure("A0", 0, "A0")
  private val failureA1 = Output.Failure("A1", 1, "A1")
  private val failureB0 = Output.Failure("B0", 0, "B0")
  private val failureB1 = Output.Failure("B1", 1, "B1")
  private val choiceParsers = listOf(
    anyOf("ab").plus() seq anyOf("12").plus(),
    anyOf("ac").plus() seq anyOf("13").plus(),
    anyOf("ad").plus() seq anyOf("14").plus(),
  ).map { it.flatten() }

  @Test
  fun test_choice() {
    val parser = or(char('a'), char('b'), char('c'))
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected", 0)
    assertFailure(parser, "", "'c' expected", 0)
  }

  @Test
  fun test_choice_or() {
    val parser = char('a') or char('b') or char('c')
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected", 0)
    assertFailure(parser, "", "'c' expected", 0)
  }

  @Test
  fun test_choice_div() {
    val parser = char('a') / char('b') / char('c')
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected", 0)
    assertFailure(parser, "", "'c' expected", 0)
  }

  @Test
  fun test_choice_toChoiceParser() {
    val parser = listOf(char('a'), char('b'), char('c')).toChoiceParser()
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected", 0)
    assertFailure(parser, "", "'c' expected", 0)
  }

  @Test
  fun test_choice_empty() {
    assertFailsWith<IllegalArgumentException> {
      emptyList<Parser<Char>>().toChoiceParser()
    }
  }

  @Test
  fun test_choice_single() {
    val parser = or(char('a'))
    expectParserInvariants(parser)
    assertEquals(1, parser.children.size)
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected", 0)
    assertFailure(parser, "", "'a' expected", 0)
  }

  @Test
  fun test_choice_construction() {
    val defaultTwo = char('a') or char('b')
    assertEquals(failureA0, defaultTwo.failureJoiner(failureA1, failureA0))
    val customTwo = char('a').or(char('b'), failureJoiner = ::selectFarthest)
    assertEquals(failureA1, customTwo.failureJoiner(failureA1, failureA0))
    val customCopy = customTwo.copy()
    assertEquals(failureA1, customCopy.failureJoiner(failureA1, failureA0))
    val customThree = char('a').or(char('b'), failureJoiner = ::selectFarthest).or(char('c'))
    assertEquals(failureA1, customThree.failureJoiner(failureA1, failureA0))
  }

  @Test
  fun test_choice_nested_preserves_joiner() {
    val right = char('b').or(char('c'), failureJoiner = ::selectFirst)
    val combined = char('a') or right
    assertEquals(2, combined.children.size)
    assertEquals(right, combined.children[1])
  }

  @Test
  fun test_choice_selectFirst() {
    val parser = or(choiceParsers, failureJoiner = ::selectFirst)
    expectParserInvariants(parser)
    assertEquals(selectFirst(failureA0, failureB0), failureA0)
    assertEquals(selectFirst(failureB0, failureA0), failureB0)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ab] expected", 0)
    assertFailure(parser, "a", "any of [12] expected", 1)
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [12] expected", 1)
    assertFailure(parser, "ad", "any of [12] expected", 1)
  }

  @Test
  fun test_choice_selectLast() {
    val parser = or(choiceParsers, failureJoiner = ::selectLast)
    expectParserInvariants(parser)
    assertEquals(selectLast(failureA0, failureB0), failureB0)
    assertEquals(selectLast(failureB0, failureA0), failureA0)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ad] expected", 0)
    assertFailure(parser, "a", "any of [14] expected", 1)
    assertFailure(parser, "ab", "any of [14] expected", 1)
    assertFailure(parser, "ac", "any of [14] expected", 1)
    assertFailure(parser, "ad", "any of [14] expected", 2)
  }

  @Test
  fun test_choice_selectFarthest() {
    val parser = or(choiceParsers, failureJoiner = ::selectFarthest)
    expectParserInvariants(parser)
    assertEquals(selectFarthest(failureA0, failureB0), failureB0)
    assertEquals(selectFarthest(failureA0, failureB1), failureB1)
    assertEquals(selectFarthest(failureB0, failureA0), failureA0)
    assertEquals(selectFarthest(failureB1, failureA0), failureB1)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ad] expected", 0)
    assertFailure(parser, "a", "any of [14] expected", 1)
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [13] expected", 2)
    assertFailure(parser, "ad", "any of [14] expected", 2)
  }

  @Test
  fun test_choice_selectFarthestJoined() {
    val parser = or(choiceParsers, failureJoiner = ::selectFarthestJoined)
    expectParserInvariants(parser)
    assertEquals(selectFarthestJoined(failureA0, failureB1), failureB1)
    assertEquals(selectFarthestJoined(failureB1, failureA0), failureB1)
    assertEquals("A0 OR B0", selectFarthestJoined(failureA0, failureB0).message)
    assertEquals("B0 OR A0", selectFarthestJoined(failureB0, failureA0).message)
    assertEquals("A1 OR B1", selectFarthestJoined(failureA1, failureB1).message)
    assertEquals("B1 OR A1", selectFarthestJoined(failureB1, failureA1).message)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(
      parser, "", "any of [ab] expected OR any of [ac] expected OR any of [ad] expected", 0
    )
    assertFailure(
      parser, "a", "any of [12] expected OR any of [13] expected OR any of [14] expected", 1
    )
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [13] expected", 2)
    assertFailure(parser, "ad", "any of [14] expected", 2)
  }

  @Test
  fun test_choice_covariance() {
    val p1: Parser<Char> = char('a')
    val p2: Parser<String> = digit().map { it.toString() }
    val choice: ChoiceParser<Any> = p1 or p2
    expectParserInvariants(choice)
    assertSuccess(choice, "a", 'a')
    assertSuccess(choice, "1", "1")
  }

  @Test
  fun test_choice_iterable_and_vararg_constructor() {
    val fromIterable = or(listOf(char('a'), char('b')))
    assertSuccess(fromIterable, "a", 'a')

    val fromVararg = ChoiceParser(char('a'), char('b'))
    assertSuccess(fromVararg, "b", 'b')

    val p1 = ChoiceParser(char('a'), char('b'), failureJoiner = ::selectFirst)
    val p2 = ChoiceParser(char('a'), char('b'), failureJoiner = ::selectFirst)
    val p3 = ChoiceParser(char('a'), char('b'), failureJoiner = ::selectLast)
    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
    kotlin.test.assertFalse(p1.isEqualTo(char('a')))
    kotlin.test.assertFalse(p1.hasEqualProperties(char('a')))
  }
}
