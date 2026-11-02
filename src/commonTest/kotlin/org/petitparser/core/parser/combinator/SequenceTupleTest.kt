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

  @Test
  fun test_sequence_then_chaining_full() {
    val p2 = char('1').then(char('2'))
    val p3 = p2.then(char('3'))
    val p4 = p3.then(char('4'))
    val p5 = p4.then(char('5'))
    val p6 = p5.then(char('6'))
    val p7 = p6.then(char('7'))
    val p8 = p7.then(char('8'))
    val p9 = p8.then(char('9'))
    assertSuccess(p9, "123456789", Tuple9('1', '2', '3', '4', '5', '6', '7', '8', '9'))
  }

  @Test
  fun test_sequence_mapN_methods() {
    val s2 = seq(char('1'), char('2'))
    assertSuccess(s2.map2(hasSideEffects = true) { a, b -> "$a$b" }, "12", "12")

    val s3 = seq(char('1'), char('2'), char('3'))
    assertSuccess(s3.map3 { a, b, c -> "$a$b$c" }, "123", "123")
    assertSuccess(s3.map3(hasSideEffects = true) { a, b, c -> "$a$b$c" }, "123", "123")

    val s4 = seq(char('1'), char('2'), char('3'), char('4'))
    assertSuccess(s4.map4 { a, b, c, d -> "$a$b$c$d" }, "1234", "1234")
    assertSuccess(s4.map4(hasSideEffects = true) { a, b, c, d -> "$a$b$c$d" }, "1234", "1234")

    val s5 = seq(char('1'), char('2'), char('3'), char('4'), char('5'))
    assertSuccess(s5.map5 { a, b, c, d, e -> "$a$b$c$d$e" }, "12345", "12345")
    assertSuccess(s5.map5(hasSideEffects = true) { a, b, c, d, e -> "$a$b$c$d$e" }, "12345", "12345")

    val s6 = seq(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'))
    assertSuccess(s6.map6 { a, b, c, d, e, f -> "$a$b$c$d$e$f" }, "123456", "123456")
    assertSuccess(s6.map6(hasSideEffects = true) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }, "123456", "123456")

    val s7 = seq(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'))
    assertSuccess(s7.map7 { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }, "1234567", "1234567")
    assertSuccess(s7.map7(hasSideEffects = true) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }, "1234567", "1234567")

    val s8 = seq(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'))
    assertSuccess(s8.map8 { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }, "12345678", "12345678")
    assertSuccess(s8.map8(hasSideEffects = true) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }, "12345678", "12345678")

    val s9 = seq(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'))
    assertSuccess(s9.map9 { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }, "123456789", "123456789")
    assertSuccess(s9.map9(hasSideEffects = true) { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }, "123456789", "123456789")
  }
}
