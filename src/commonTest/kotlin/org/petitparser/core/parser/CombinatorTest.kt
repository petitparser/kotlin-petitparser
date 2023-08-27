package org.petitparser.core.parser

import org.petitparser.core.context.Output
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.combinator.and
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.not
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.skip
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.anyOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.utils.selectFarthest
import org.petitparser.core.parser.utils.selectFarthestJoined
import org.petitparser.core.parser.utils.selectFirst
import org.petitparser.core.parser.utils.selectLast
import kotlin.test.Test
import kotlin.test.assertEquals

val failureA0 = Output.Failure("A0", 0, "A0")
val failureA1 = Output.Failure("A1", 1, "A1")
val failureB0 = Output.Failure("B0", 0, "B0")
val failureB1 = Output.Failure("B1", 1, "B1")

val choiceParsers = listOf(
  anyOf("ab").plus() seq anyOf("12").plus(),
  anyOf("ac").plus() seq anyOf("13").plus(),
  anyOf("ad").plus() seq anyOf("14").plus(),
).map { it.flatten() }.toTypedArray()

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
  fun test_choice_selectFirst() {
    val parser = or(*choiceParsers, failureJoiner = ::selectFirst)
    assertEquals(selectFirst(failureA0, failureB0), failureA0)
    assertEquals(selectFirst(failureB0, failureA0), failureB0)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ab] expected")
    assertFailure(parser, "a", "any of [12] expected", 1)
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [12] expected", 1)
    assertFailure(parser, "ad", "any of [12] expected", 1)
  }

  @Test
  fun test_choice_selectLast() {
    val parser = or(*choiceParsers, failureJoiner = ::selectLast)
    assertEquals(selectLast(failureA0, failureB0), failureB0)
    assertEquals(selectLast(failureB0, failureA0), failureA0)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ad] expected")
    assertFailure(parser, "a", "any of [14] expected", 1)
    assertFailure(parser, "ab", "any of [14] expected", 1)
    assertFailure(parser, "ac", "any of [14] expected", 1)
    assertFailure(parser, "ad", "any of [14] expected", 2)
  }

  @Test
  fun test_choice_selectFarthest() {
    val parser = or(*choiceParsers, failureJoiner = ::selectFarthest)
    assertEquals(selectFarthest(failureA0, failureB0), failureB0)
    assertEquals(selectFarthest(failureA0, failureB1), failureB1)
    assertEquals(selectFarthest(failureB0, failureA0), failureA0)
    assertEquals(selectFarthest(failureB1, failureA0), failureB1)
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(parser, "", "any of [ad] expected")
    assertFailure(parser, "a", "any of [14] expected", 1)
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [13] expected", 2)
    assertFailure(parser, "ad", "any of [14] expected", 2)
  }

  @Test
  fun test_choice_selectFarthestJoined() {
    val parser = or(*choiceParsers, failureJoiner = ::selectFarthestJoined)
    assertEquals(selectFarthestJoined(failureA0, failureB1), failureB1)
    assertEquals(selectFarthestJoined(failureB1, failureA0), failureB1)
    assertEquals(selectFarthestJoined(failureA0, failureB0).message, "A0 OR B0")
    assertEquals(selectFarthestJoined(failureB0, failureA0).message, "B0 OR A0")
    assertEquals(selectFarthestJoined(failureA1, failureB1).message, "A1 OR B1")
    assertEquals(selectFarthestJoined(failureB1, failureA1).message, "B1 OR A1")
    assertSuccess(parser, "ab12", "ab12")
    assertSuccess(parser, "ac13", "ac13")
    assertSuccess(parser, "ad14", "ad14")
    assertFailure(
      parser, "", "any of [ab] expected OR any of [ac] expected OR any of [ad] expected"
    )
    assertFailure(
      parser, "a", "any of [12] expected OR any of [13] expected OR any of [14] expected", 1
    )
    assertFailure(parser, "ab", "any of [12] expected", 2)
    assertFailure(parser, "ac", "any of [13] expected", 2)
    assertFailure(parser, "ad", "any of [14] expected", 2)
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
  fun test_sequence_seqMap2() {
    val parser = seqMap(
      char('1'),
      char('2'),
    ) { a, b -> listOf(a, b) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertSuccess(parser, "12", listOf('1', '2'))
  }

  @Test
  fun test_sequence_seqMap3() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
    ) { a, b, c -> listOf(a, b, c) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertSuccess(parser, "123", listOf('1', '2', '3'))
  }

  @Test
  fun test_sequence_seqMap4() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
    ) { a, b, c, d -> listOf(a, b, c, d) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
  }

  @Test
  fun test_sequence_seqMap5() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
    ) { a, b, c, d, e -> listOf(a, b, c, d, e) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertSuccess(parser, "12345", listOf('1', '2', '3', '4', '5'))
  }

  @Test
  fun test_sequence_seqMap6() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
    ) { a, b, c, d, e, f -> listOf(a, b, c, d, e, f) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertSuccess(parser, "123456", listOf('1', '2', '3', '4', '5', '6'))
  }

  @Test
  fun test_sequence_seqMap7() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
    ) { a, b, c, d, e, f, g -> listOf(a, b, c, d, e, f, g) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertFailure(parser, "123456", "'7' expected", 6)
    assertSuccess(parser, "1234567", listOf('1', '2', '3', '4', '5', '6', '7'))
  }

  @Test
  fun test_sequence_seqMap8() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
      char('8'),
    ) { a, b, c, d, e, f, g, h -> listOf(a, b, c, d, e, f, g, h) }
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertFailure(parser, "123456", "'7' expected", 6)
    assertFailure(parser, "1234567", "'8' expected", 7)
    assertSuccess(parser, "12345678", listOf('1', '2', '3', '4', '5', '6', '7', '8'))
  }

  @Test
  fun test_settable() {
    val parser = digit().settable()
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected")
    parser.delegate = letter()
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "1", "letter expected")
  }

  @Test
  fun test_undefined() {
    val parser = undefined<Char>()
    assertFailure(parser, "1", "undefined parser")
    parser.delegate = digit()
    assertSuccess(parser, "1", '1')
  }

  @Test
  fun test_skip() {
    val parser = digit().skip()
    assertSuccess(parser, "1", '1')
    assertFailure(parser, ">2", "digit expected")
  }

  @Test
  fun test_skip_before() {
    val parser = digit().skip(before = char('>'))
    assertFailure(parser, "1", "'>' expected")
    assertFailure(parser, ">", "digit expected")
    assertSuccess(parser, ">3", '3')
  }

  @Test
  fun test_skip_after() {
    val parser = digit().skip(after = char('<'))
    assertFailure(parser, "1", "'<' expected")
    assertFailure(parser, ">2", "digit expected")
    assertSuccess(parser, "3<", '3')
  }

  @Test
  fun test_skip_before_after() {
    val parser = digit().skip(before = char('>'), after = char('<'))
    assertFailure(parser, "1", "'>' expected")
    assertFailure(parser, ">", "digit expected")
    assertFailure(parser, ">3", "'<' expected")
    assertSuccess(parser, ">4<", '4')
  }
}