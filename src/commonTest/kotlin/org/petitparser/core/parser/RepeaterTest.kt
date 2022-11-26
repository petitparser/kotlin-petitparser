package org.petitparser.core.parser

import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.consumer.letterOrDigit
import org.petitparser.core.parser.repeater.SeparatedList
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.plusGreedy
import org.petitparser.core.parser.repeater.plusLazy
import org.petitparser.core.parser.repeater.plusSeparated
import org.petitparser.core.parser.repeater.repeat
import org.petitparser.core.parser.repeater.repeatGreedy
import org.petitparser.core.parser.repeater.repeatLazy
import org.petitparser.core.parser.repeater.repeatSeparated
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.starGreedy
import org.petitparser.core.parser.repeater.starLazy
import org.petitparser.core.parser.repeater.starSeparated
import org.petitparser.core.parser.repeater.times
import org.petitparser.core.parser.repeater.timesSeparated
import kotlin.test.Test

internal class RepeaterTest {
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
  fun test_lazy_star() {
    val parser = letterOrDigit().starLazy(digit())
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
    val parser = letterOrDigit().plusLazy(digit())
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
    val parser = digit().times(3)
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
    val parser = digit().starSeparated(letter())
    assertSuccess(parser, "", SeparatedList(listOf(), listOf()))
    assertSuccess(parser, "a", SeparatedList(listOf(), listOf()), 0)
    assertSuccess(parser, "1", SeparatedList(listOf('1'), listOf()))
    assertSuccess(parser, "1a", SeparatedList(listOf('1'), listOf()), 1)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3;", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')))
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')), 7)
  }

  @Test
  fun test_separated_plus() {
    val parser = digit().plusSeparated(letter())
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertSuccess(parser, "1", SeparatedList(listOf('1'), listOf()))
    assertSuccess(parser, "1a", SeparatedList(listOf('1'), listOf()), 1)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')))
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')), 7)
  }

  @Test
  fun test_separated_count() {
    val parser = digit().timesSeparated(letter(), 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "1a", "digit expected", 2)
    assertFailure(parser, "1a2", "letter expected", 3)
    assertFailure(parser, "1a2b", "digit expected", 4)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
  }

  @Test
  fun test_separated_min_max() {
    val parser = digit().repeatSeparated(letter(), 2, 3)
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "1a", "digit expected", 2)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
  }
}