package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.utils.Tuple2
import org.petitparser.core.parser.utils.Tuple3
import org.petitparser.core.parser.utils.Tuple4
import org.petitparser.core.parser.utils.Tuple5
import org.petitparser.core.parser.utils.Tuple6
import org.petitparser.core.parser.utils.Tuple7
import org.petitparser.core.parser.utils.Tuple8
import org.petitparser.core.parser.utils.Tuple9
import kotlin.test.Test

class SequenceTupleTest {
  @Test
  fun test_sequence_tuple2() {
    val parser = seq(char('1'), char('2'))
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertSuccess(parser, "12", Tuple2('1', '2'))
    assertSuccess(parser, "12*", Tuple2('1', '2'), 2)
  }

  @Test
  fun test_sequence_tuple2_destructuring() {
    val parser = seq(char('1'), char('2')).map { (a, b) -> "$a-$b" }
    expectParserInvariants(parser)
    assertSuccess(parser, "12", "1-2")
  }

  @Test
  fun test_sequence_tuple2_map2() {
    val parser = seq(char('1'), char('2')).map2 { a, b -> "$a-$b" }
    expectParserInvariants(parser)
    assertSuccess(parser, "12", "1-2")
  }

  @Test
  fun test_sequence_then_chaining() {
    val p2 = char('1').then(char('2'))
    expectParserInvariants(p2)
    val p3 = p2.then(char('3'))
    expectParserInvariants(p3)
    val p4 = p3.then(char('4'))
    expectParserInvariants(p4)
    assertSuccess(p4, "1234", Tuple4('1', '2', '3', '4'))
  }

  @Test
  fun test_sequence_tuple3() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertSuccess(parser, "123", Tuple3('1', '2', '3'))
    assertSuccess(parser, "123*", Tuple3('1', '2', '3'), 3)
  }

  @Test
  fun test_sequence_tuple3_map3() {
    val parser = seq(char('1'), char('2'), char('3')).map3 { a, b, c -> "$a$b$c" }
    expectParserInvariants(parser)
    assertSuccess(parser, "123", "123")
  }

  @Test
  fun test_sequence_tuple4() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertSuccess(parser, "1234", Tuple4('1', '2', '3', '4'))
  }

  @Test
  fun test_sequence_tuple5() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertSuccess(parser, "12345", Tuple5('1', '2', '3', '4', '5'))
  }

  @Test
  fun test_sequence_tuple6() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertSuccess(parser, "123456", Tuple6('1', '2', '3', '4', '5', '6'))
  }

  @Test
  fun test_sequence_tuple7() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertFailure(parser, "123456", "'7' expected", 6)
    assertSuccess(parser, "1234567", Tuple7('1', '2', '3', '4', '5', '6', '7'))
  }

  @Test
  fun test_sequence_tuple8() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
      char('8'),
    )
    expectParserInvariants(parser)
    assertFailure(parser, "", "'1' expected", 0)
    assertFailure(parser, "1", "'2' expected", 1)
    assertFailure(parser, "12", "'3' expected", 2)
    assertFailure(parser, "123", "'4' expected", 3)
    assertFailure(parser, "1234", "'5' expected", 4)
    assertFailure(parser, "12345", "'6' expected", 5)
    assertFailure(parser, "123456", "'7' expected", 6)
    assertFailure(parser, "1234567", "'8' expected", 7)
    assertSuccess(parser, "12345678", Tuple8('1', '2', '3', '4', '5', '6', '7', '8'))
  }

  @Test
  fun test_sequence_tuple9() {
    val parser = seq(
      char('1'),
      char('2'),
      char('3'),
      char('4'),
      char('5'),
      char('6'),
      char('7'),
      char('8'),
      char('9'),
    )
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
    assertSuccess(parser, "123456789", Tuple9('1', '2', '3', '4', '5', '6', '7', '8', '9'))
  }
}
