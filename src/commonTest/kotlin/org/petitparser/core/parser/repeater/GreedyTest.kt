package org.petitparser.core.parser.repeater

import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letterOrDigit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GreedyTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(letterOrDigit().starGreedy(digit()))
    expectParserInvariants(letterOrDigit().plusGreedy(digit()))
    expectParserInvariants(letterOrDigit().repeatGreedy(digit(), 2, 4))
  }

  @Test
  fun test_greedy_star() {
    val parser = letterOrDigit().starGreedy(digit())
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "ab", "digit expected", 0)
    assertSuccess(parser, "1", listOf(), 0)
    assertSuccess(parser, "a1", listOf('a'), 1)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "12", listOf('1'), 1)
    assertSuccess(parser, "a12", listOf('a', '1'), 2)
    assertSuccess(parser, "ab12", listOf('a', 'b', '1'), 3)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c', '1'), 4)
    assertSuccess(parser, "123", listOf('1', '2'), 2)
    assertSuccess(parser, "a123", listOf('a', '1', '2'), 3)
    assertSuccess(parser, "ab123", listOf('a', 'b', '1', '2'), 4)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c', '1', '2'), 5)
  }

  @Test
  fun test_greedy_plus() {
    val parser = letterOrDigit().plusGreedy(digit())
    assertFailure(parser, "", "letter or digit expected", 0)
    assertFailure(parser, "a", "digit expected", 1)
    assertFailure(parser, "ab", "digit expected", 1)
    assertFailure(parser, "1", "digit expected", 1)
    assertSuccess(parser, "a1", listOf('a'), 1)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "12", listOf('1'), 1)
    assertSuccess(parser, "a12", listOf('a', '1'), 2)
    assertSuccess(parser, "ab12", listOf('a', 'b', '1'), 3)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c', '1'), 4)
    assertSuccess(parser, "123", listOf('1', '2'), 2)
    assertSuccess(parser, "a123", listOf('a', '1', '2'), 3)
    assertSuccess(parser, "ab123", listOf('a', 'b', '1', '2'), 4)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c', '1', '2'), 5)
  }

  @Test
  fun test_greedy_repeat() {
    val parser = letterOrDigit().repeatGreedy(digit(), 2, 4)
    assertFailure(parser, "", "letter or digit expected", 0)
    assertFailure(parser, "a", "letter or digit expected", 1)
    assertFailure(parser, "ab", "digit expected", 2)
    assertFailure(parser, "abc", "digit expected", 2)
    assertFailure(parser, "abcd", "digit expected", 2)
    assertFailure(parser, "abcde", "digit expected", 2)
    assertFailure(parser, "1", "letter or digit expected", 1)
    assertFailure(parser, "a1", "digit expected", 2)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "abcd1", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde1", "digit expected", 2)
    assertFailure(parser, "12", "digit expected", 2)
    assertSuccess(parser, "a12", listOf('a', '1'), 2)
    assertSuccess(parser, "ab12", listOf('a', 'b', '1'), 3)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c', '1'), 4)
    assertSuccess(parser, "abcd12", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde12", "digit expected", 2)
    assertSuccess(parser, "123", listOf('1', '2'), 2)
    assertSuccess(parser, "a123", listOf('a', '1', '2'), 3)
    assertSuccess(parser, "ab123", listOf('a', 'b', '1', '2'), 4)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c', '1'), 4)
    assertSuccess(parser, "abcd123", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde123", "digit expected", 2)
  }

  @Test
  fun test_greedy_no_limit_match() {
    val parser = char('a').starGreedy(char('b'))
    assertFailure(parser, "aaaa", "'b' expected", 0)
  }

  @Test
  fun test_greedy_bounds() {
    val error1 = assertFailsWith<IllegalArgumentException> {
      digit().repeatGreedy(letterOrDigit(), -1, 2)
    }
    assertEquals("min must be at least 0, but got -1", error1.message)

    val error2 = assertFailsWith<IllegalArgumentException> {
      digit().repeatGreedy(letterOrDigit(), 3, 2)
    }
    assertEquals("max must be at least 3, but got 2", error2.message)
  }

  @Test
  fun test_greedy_default_max() {
    val parser = letterOrDigit().repeatGreedy(digit(), 2)
    assertEquals(2, parser.min)
    assertEquals(2, parser.max)
  }

  @Test
  fun test_greedy_toString() {
    assertEquals("GreedyRepeatingParser[0..*]", letterOrDigit().starGreedy(digit()).toString())
    assertEquals("GreedyRepeatingParser[1..*]", letterOrDigit().plusGreedy(digit()).toString())
    assertEquals("GreedyRepeatingParser[2..4]", letterOrDigit().repeatGreedy(digit(), 2, 4).toString())
  }

  @Test
  fun test_greedy_equality() {
    val p1 = digit().repeatGreedy(letterOrDigit(), 1, 3)
    val p2 = digit().repeatGreedy(letterOrDigit(), 1, 3)
    val pDiffMin = digit().repeatGreedy(letterOrDigit(), 2, 3)
    val pDiffMax = digit().repeatGreedy(letterOrDigit(), 1, 4)
    val pDiffLimit = digit().repeatGreedy(letterOrDigit().star(), 1, 3)
    val pDiffDelegate = digit().star().repeatGreedy(letterOrDigit(), 1, 3)

    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffMin))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffMax))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffLimit))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffDelegate))
  }

  @Test
  fun test_greedy_unbounded() {
    val count = 10_000
    val input = "a".repeat(count) + "1"
    val parser = letterOrDigit().repeatGreedy(digit(), 2, Int.MAX_VALUE)
    val expected = List(count) { 'a' }
    assertSuccess(parser, input, expected, count)
  }

  @Test
  fun test_infinite_loop_protection() {
    val emptyParser = Parser { it.success(Unit) }
    val starParser = emptyParser.starGreedy(digit())
    val error1 = assertFailsWith<IllegalStateException> {
      starParser.parseOn(org.petitparser.core.context.Input.Impl("1", 0))
    }
    assertEquals("$emptyParser must always consume", error1.message)

    val error2 = assertFailsWith<IllegalStateException> {
      starParser.fastParseOn("1", 0)
    }
    assertEquals("$emptyParser must always consume", error2.message)

    val plusParser = emptyParser.plusGreedy(digit())
    val error3 = assertFailsWith<IllegalStateException> {
      plusParser.parseOn(org.petitparser.core.context.Input.Impl("1", 0))
    }
    assertEquals("$emptyParser must always consume", error3.message)

    val error4 = assertFailsWith<IllegalStateException> {
      plusParser.fastParseOn("1", 0)
    }
    assertEquals("$emptyParser must always consume", error4.message)
  }
}
