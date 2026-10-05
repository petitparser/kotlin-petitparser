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

class LazyTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(letterOrDigit().starLazy(digit()))
    expectParserInvariants(letterOrDigit().plusLazy(digit()))
    expectParserInvariants(letterOrDigit().repeatLazy(digit(), 2, 4))
  }

  @Test
  fun test_lazy_star() {
    val parser = letterOrDigit().starLazy(digit())
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 1)
    assertFailure(parser, "ab", "digit expected", 2)
    assertSuccess(parser, "1", listOf(), 0)
    assertSuccess(parser, "a1", listOf('a'), 1)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "12", listOf(), 0)
    assertSuccess(parser, "a12", listOf('a'), 1)
    assertSuccess(parser, "ab12", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "123", listOf(), 0)
    assertSuccess(parser, "a123", listOf('a'), 1)
    assertSuccess(parser, "ab123", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c'), 3)
  }

  @Test
  fun test_lazy_plus() {
    val parser = letterOrDigit().plusLazy(digit())
    assertFailure(parser, "", "letter or digit expected", 0)
    assertFailure(parser, "a", "digit expected", 1)
    assertFailure(parser, "ab", "digit expected", 2)
    assertFailure(parser, "1", "digit expected", 1)
    assertSuccess(parser, "a1", listOf('a'), 1)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "12", listOf('1'), 1)
    assertSuccess(parser, "a12", listOf('a'), 1)
    assertSuccess(parser, "ab12", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "123", listOf('1'), 1)
    assertSuccess(parser, "a123", listOf('a'), 1)
    assertSuccess(parser, "ab123", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c'), 3)
  }

  @Test
  fun test_lazy_repeat() {
    val parser = letterOrDigit().repeatLazy(digit(), 2, 4)
    assertFailure(parser, "", "letter or digit expected", 0)
    assertFailure(parser, "a", "letter or digit expected", 1)
    assertFailure(parser, "ab", "digit expected", 2)
    assertFailure(parser, "abc", "digit expected", 3)
    assertFailure(parser, "abcd", "digit expected", 4)
    assertFailure(parser, "abcde", "digit expected", 4)
    assertFailure(parser, "1", "letter or digit expected", 1)
    assertFailure(parser, "a1", "digit expected", 2)
    assertSuccess(parser, "ab1", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc1", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "abcd1", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde1", "digit expected", 4)
    assertFailure(parser, "12", "digit expected", 2)
    assertSuccess(parser, "a12", listOf('a', '1'), 2)
    assertSuccess(parser, "ab12", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc12", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "abcd12", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde12", "digit expected", 4)
    assertSuccess(parser, "123", listOf('1', '2'), 2)
    assertSuccess(parser, "a123", listOf('a', '1'), 2)
    assertSuccess(parser, "ab123", listOf('a', 'b'), 2)
    assertSuccess(parser, "abc123", listOf('a', 'b', 'c'), 3)
    assertSuccess(parser, "abcd123", listOf('a', 'b', 'c', 'd'), 4)
    assertFailure(parser, "abcde123", "digit expected", 4)
  }

  @Test
  fun test_lazy_no_limit_match() {
    val parser = char('a').starLazy(char('b'))
    assertFailure(parser, "aaaa", "'b' expected", 4)
  }

  @Test
  fun test_lazy_bounds() {
    val error1 = assertFailsWith<IllegalArgumentException> {
      digit().repeatLazy(letterOrDigit(), -1, 2)
    }
    assertEquals("min must be at least 0, but got -1", error1.message)

    val error2 = assertFailsWith<IllegalArgumentException> {
      digit().repeatLazy(letterOrDigit(), 3, 2)
    }
    assertEquals("max must be at least 3, but got 2", error2.message)
  }

  @Test
  fun test_lazy_default_max() {
    val parser = letterOrDigit().repeatLazy(digit(), 2)
    assertEquals(2, parser.min)
    assertEquals(2, parser.max)
  }

  @Test
  fun test_lazy_toString() {
    assertEquals("LazyRepeatingParser[0..*]", letterOrDigit().starLazy(digit()).toString())
    assertEquals("LazyRepeatingParser[1..*]", letterOrDigit().plusLazy(digit()).toString())
    assertEquals("LazyRepeatingParser[2..4]", letterOrDigit().repeatLazy(digit(), 2, 4).toString())
  }

  @Test
  fun test_lazy_equality() {
    val p1 = digit().repeatLazy(letterOrDigit(), 1, 3)
    val p2 = digit().repeatLazy(letterOrDigit(), 1, 3)
    val pDiffMin = digit().repeatLazy(letterOrDigit(), 2, 3)
    val pDiffMax = digit().repeatLazy(letterOrDigit(), 1, 4)
    val pDiffLimit = digit().repeatLazy(letterOrDigit().star(), 1, 3)
    val pDiffDelegate = digit().star().repeatLazy(letterOrDigit(), 1, 3)

    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffMin))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffMax))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffLimit))
    kotlin.test.assertFalse(p1.isEqualTo(pDiffDelegate))
  }

  @Test
  fun test_lazy_unbounded() {
    val count = 10_000
    val input = "a".repeat(count) + "1"
    val parser = letterOrDigit().repeatLazy(digit(), 2, Int.MAX_VALUE)
    val expected = List(count) { 'a' }
    assertSuccess(parser, input, expected, count)
  }

  @Test
  fun test_infinite_loop_protection() {
    val emptyParser = Parser { it.success(Unit) }
    val starParser = emptyParser.starLazy(digit())
    val error1 = assertFailsWith<IllegalStateException> {
      starParser.parseOn(org.petitparser.core.context.Input.Impl("a", 0))
    }
    assertEquals("$emptyParser must always consume", error1.message)

    val error2 = assertFailsWith<IllegalStateException> {
      starParser.fastParseOn("a", 0)
    }
    assertEquals("$emptyParser must always consume", error2.message)

    val plusParser = emptyParser.plusLazy(digit())
    val error3 = assertFailsWith<IllegalStateException> {
      plusParser.parseOn(org.petitparser.core.context.Input.Impl("a", 0))
    }
    assertEquals("$emptyParser must always consume", error3.message)

    val error4 = assertFailsWith<IllegalStateException> {
      plusParser.fastParseOn("a", 0)
    }
    assertEquals("$emptyParser must always consume", error4.message)
  }
}
