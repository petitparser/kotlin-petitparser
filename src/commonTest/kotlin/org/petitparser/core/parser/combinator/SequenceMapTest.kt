package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test

class SequenceMapTest {
  @Test
  fun test_sequence_seqMap2() {
    val parser = seqMap(
      char('1'),
      char('2'),
    ) { a, b -> listOf(a, b) }
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "12*", listOf('1', '2'), 2)
  }

  @Test
  fun test_sequence_seqMap2_hasSideEffects() {
    var count = 0
    val parser = seqMap(
      char('1'),
      char('2'),
      hasSideEffects = true,
    ) { a, b ->
      count++
      listOf(a, b)
    }
    expectParserInvariants(parser)
    assertSuccess(parser, "12", listOf('1', '2'))
  }

  @Test
  fun test_sequence_seqMap3() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
    ) { a, b, c -> listOf(a, b, c) }
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "123*", listOf('1', '2', '3'), 3)
  }

  @Test
  fun test_sequence_seqMap4() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
    ) { a, b, c, d -> listOf(a, b, c, d) }
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
  fun test_sequence_seqMap9() {
    val parser = seqMap(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
      char('8'),
      char('9'),
    ) { a, b, c, d, e, f, g, h, i -> listOf(a, b, c, d, e, f, g, h, i) }
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertFailure(parser, "123456", "'7' expected", 6)
    assertFailure(parser, "1234567", "'8' expected", 7)
    assertFailure(parser, "12345678", "'9' expected", 8)
    assertSuccess(parser, "123456789", listOf('1', '2', '3', '4', '5', '6', '7', '8', '9'))
  }
}
