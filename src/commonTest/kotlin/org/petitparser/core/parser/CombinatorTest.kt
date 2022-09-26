package org.petitparser.core.parser

import org.petitparser.core.parser.combinator.*
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letterOrDigit
import kotlin.test.Test

internal class CombinatorTest {
  @Test
  fun test_and() {
    val parser = digit().and()
    assertSuccess(parser, "1", '1', 0)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_choice() {
    val parser = or(char('a'), char('b'), char('c'))
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected")
    assertFailure(parser, "", "'c' expected")
  }

  @Test
  fun test_choice_or() {
    val parser = char('a') or char('b') or char('c')
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected")
    assertFailure(parser, "", "'c' expected")
  }

  @Test
  fun test_choice_div() {
    val parser = char('a') / char('b') / char('c')
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "'c' expected")
    assertFailure(parser, "", "'c' expected")
  }

  @Test
  fun test_greedy_star() {
    val parser = letterOrDigit().greedyStar(digit())
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
    val parser = letterOrDigit().greedyPlus(digit())
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
    val parser = letterOrDigit().greedyRepeat(digit(), 2, 4)
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
  fun test_lazy_star() {
    val parser = letterOrDigit().lazyStar(digit())
    assertFailure(parser, "", "digit expected")
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
    val parser = letterOrDigit().lazyPlus(digit())
    assertFailure(parser, "", "letter or digit expected")
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
    val parser = letterOrDigit().lazyRepeat(digit(), 2, 4)
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
  fun test_not() {
    val parser = digit().not("no digit expected")
    assertFailure(parser, "1", "no digit expected", 0)
    val result = parser.parse("a")
    assertSuccess(result, result.value, 0)
    assertFailure(result.value, "digit expected", 0)
  }

  @Test
  fun test_optional() {
    val parser = digit().optional()
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "a", null, 0)
  }

  @Test
  fun test_optionalWith() {
    val parser = digit().optional('*')
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "a", '*', 0)
  }

  @Test
  fun test_repeat_star() {
    val parser = digit().star()
    assertSuccess(parser, "", listOf())
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
  }

  @Test
  fun test_repeat_plus() {
    val parser = digit().plus()
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_repeat_count() {
    val parser = digit().repeat(3)
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3'), 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "digit expected", 1)
    assertFailure(parser, "12", "digit expected", 2)
  }

  @Test
  fun test_repeat_min_max() {
    val parser = digit().repeat(2, 3)
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "digit expected", 1)
  }

  @Test
  fun test_sequence() {
    val parser = seq(char('a'), char('b'), char('c'))
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }

  @Test
  fun test_sequence_seq() {
    val parser = char('a') seq char('b') seq char('c')
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }

  @Test
  fun test_sequence_plus() {
    val parser = char('a') + char('b') + char('c')
    assertSuccess(parser, "abc", listOf('a', 'b', 'c'))
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "a", "'b' expected", 1)
    assertFailure(parser, "ab", "'c' expected", 2)
    assertFailure(parser, "*bc", "'a' expected", 0)
    assertFailure(parser, "a*c", "'b' expected", 1)
    assertFailure(parser, "ab*", "'c' expected", 2)
  }
}