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
  fun test_infinite_loop_protection() {
    val emptyParser = Parser { it.success(Unit) }
    val parser = emptyParser.starLazy(digit())
    val error1 = assertFailsWith<IllegalStateException> {
      parser.parseOn(org.petitparser.core.context.Input.Impl("a", 0))
    }
    assertEquals("$emptyParser must always consume", error1.message)

    val error2 = assertFailsWith<IllegalStateException> {
      parser.fastParseOn("a", 0)
    }
    assertEquals("$emptyParser must always consume", error2.message)
  }
}
