package org.petitparser.core.parser

import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letterOrDigit
import org.petitparser.core.parser.repeater.greedyPlus
import org.petitparser.core.parser.repeater.greedyRepeat
import org.petitparser.core.parser.repeater.greedyStar
import org.petitparser.core.parser.repeater.lazyPlus
import org.petitparser.core.parser.repeater.lazyRepeat
import org.petitparser.core.parser.repeater.lazyStar
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.repeat
import org.petitparser.core.parser.repeater.separatedPlus
import org.petitparser.core.parser.repeater.separatedRepeat
import org.petitparser.core.parser.repeater.separatedStar
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test

internal class RepeaterTest {
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
    assertFailure(parser, "", "digit expected")
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
  }

  @Test
  fun test_repeat_count() {
    val parser = digit().repeat(3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "digit expected", 1)
    assertFailure(parser, "12", "digit expected", 2)
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3'), 3)
  }

  @Test
  fun test_repeat_min_max() {
    val parser = digit().repeat(2, 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "digit expected", 1)
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3'), 3)
  }

  @Test
  fun test_separated_star() {
    val parser = digit().separatedStar(char(';'))
    assertSuccess(parser, "", listOf())
    assertSuccess(parser, ";", listOf(), 0)
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "1;", listOf('1'), 1)
    assertSuccess(parser, "1;2", listOf('1', '2'))
    assertSuccess(parser, "1;2;", listOf('1', '2'), 3)
    assertSuccess(parser, "1;2;3", listOf('1', '2', '3'))
    assertSuccess(parser, "1;2;3;", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4", listOf('1', '2', '3', '4'))
    assertSuccess(parser, "1;2;3;4;", listOf('1', '2', '3', '4'), 7)
  }

  @Test
  fun test_separated_plus() {
    val parser = digit().separatedPlus(char(';'))
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, ";", "digit expected")
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "1;", listOf('1'), 1)
    assertSuccess(parser, "1;2", listOf('1', '2'))
    assertSuccess(parser, "1;2;", listOf('1', '2'), 3)
    assertSuccess(parser, "1;2;3", listOf('1', '2', '3'))
    assertSuccess(parser, "1;2;3;", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4", listOf('1', '2', '3', '4'))
    assertSuccess(parser, "1;2;3;4;", listOf('1', '2', '3', '4'), 7)
  }

  @Test
  fun test_separated_count() {
    val parser = digit().separatedRepeat(char(';'), 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, ";", "digit expected")
    assertFailure(parser, "1", "';' expected", 1)
    assertFailure(parser, "1;", "digit expected", 2)
    assertFailure(parser, "1;2", "';' expected", 3)
    assertFailure(parser, "1;2;", "digit expected", 4)
    assertSuccess(parser, "1;2;3", listOf('1', '2', '3'))
    assertSuccess(parser, "1;2;3;", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4;", listOf('1', '2', '3'), 5)
  }

  @Test
  fun test_separated_min_max() {
    val parser = digit().separatedRepeat(char(';'), 2, 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, ";", "digit expected")
    assertFailure(parser, "1", "';' expected", 1)
    assertFailure(parser, "1;", "digit expected", 2)
    assertSuccess(parser, "1;2", listOf('1', '2'))
    assertSuccess(parser, "1;2;", listOf('1', '2'), 3)
    assertSuccess(parser, "1;2;3", listOf('1', '2', '3'))
    assertSuccess(parser, "1;2;3;", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4", listOf('1', '2', '3'), 5)
    assertSuccess(parser, "1;2;3;4;", listOf('1', '2', '3'), 5)
  }
}