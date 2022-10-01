package org.petitparser.core.parser

import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.and
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.not
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import kotlin.test.Test
import kotlin.test.assertEquals

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
    assertEquals(parser.children.size, 2)
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
    assertEquals(parser.children.size, 3)
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
    assertEquals(parser.children.size, 4)
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
    assertEquals(parser.children.size, 5)
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
    assertEquals(parser.children.size, 6)
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
    assertEquals(parser.children.size, 7)
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
    assertEquals(parser.children.size, 8)
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
}