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

  @Test
  fun test_sequence_seqMap_hasSideEffects_3_to_9() {
    val p3 = seqMap(char('1'), char('2'), char('3'), hasSideEffects = true) { a, b, c -> "$a$b$c" }
    expectParserInvariants(p3)
    assertSuccess(p3, "123", "123")

    val p4 = seqMap(char('1'), char('2'), char('3'), char('4'), hasSideEffects = true) { a, b, c, d -> "$a$b$c$d" }
    expectParserInvariants(p4)
    assertSuccess(p4, "1234", "1234")

    val p5 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), hasSideEffects = true) { a, b, c, d, e -> "$a$b$c$d$e" }
    expectParserInvariants(p5)
    assertSuccess(p5, "12345", "12345")

    val p6 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), hasSideEffects = true) { a, b, c, d, e, f -> "$a$b$c$d$e$f" }
    expectParserInvariants(p6)
    assertSuccess(p6, "123456", "123456")

    val p7 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), hasSideEffects = true) { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }
    expectParserInvariants(p7)
    assertSuccess(p7, "1234567", "1234567")

    val p8 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), hasSideEffects = true) { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
    expectParserInvariants(p8)
    assertSuccess(p8, "12345678", "12345678")

    val p9 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'), hasSideEffects = true) { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }
    expectParserInvariants(p9)
    assertSuccess(p9, "123456789", "123456789")
  }

  @Test
  fun test_sequence_seqMap_equality() {
    val cb1: (Char, Char) -> String = { a, b -> "$a$b" }
    val cb2: (Char, Char) -> String = { a, b -> "$b$a" }
    val p1 = seqMap(char('1'), char('2'), block = cb1)
    val p2 = seqMap(char('1'), char('2'), block = cb1)
    val p3 = seqMap(char('1'), char('2'), block = cb2)
    val pSide = seqMap(char('1'), char('2'), hasSideEffects = true, block = cb1)

    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
    kotlin.test.assertFalse(p1.isEqualTo(pSide))
    kotlin.test.assertFalse(p1.isEqualTo(char('1')))
  }

  @Test
  fun test_sequence_seqMap_equality_all_arities() {
    val cb1_3: (Char, Char, Char) -> String = { a, b, c -> "$a$b$c" }
    val cb2_3: (Char, Char, Char) -> String = { a, b, c -> "$c$b$a" }
    val p3_1 = seqMap(char('1'), char('2'), char('3'), block = cb1_3)
    val p3_2 = seqMap(char('1'), char('2'), char('3'), block = cb1_3)
    val p3_3 = seqMap(char('1'), char('2'), char('3'), block = cb2_3)
    val p3_side = seqMap(char('1'), char('2'), char('3'), hasSideEffects = true, block = cb1_3)
    kotlin.test.assertTrue(p3_1.isEqualTo(p3_2))
    kotlin.test.assertFalse(p3_1.isEqualTo(p3_3))
    kotlin.test.assertFalse(p3_1.isEqualTo(p3_side))

    val cb1_4: (Char, Char, Char, Char) -> String = { a, b, c, d -> "$a$b$c$d" }
    val cb2_4: (Char, Char, Char, Char) -> String = { a, b, c, d -> "$d$c$b$a" }
    val p4_1 = seqMap(char('1'), char('2'), char('3'), char('4'), block = cb1_4)
    val p4_2 = seqMap(char('1'), char('2'), char('3'), char('4'), block = cb1_4)
    val p4_3 = seqMap(char('1'), char('2'), char('3'), char('4'), block = cb2_4)
    val p4_side = seqMap(char('1'), char('2'), char('3'), char('4'), hasSideEffects = true, block = cb1_4)
    kotlin.test.assertTrue(p4_1.isEqualTo(p4_2))
    kotlin.test.assertFalse(p4_1.isEqualTo(p4_3))
    kotlin.test.assertFalse(p4_1.isEqualTo(p4_side))

    val cb1_5: (Char, Char, Char, Char, Char) -> String = { a, b, c, d, e -> "$a$b$c$d$e" }
    val cb2_5: (Char, Char, Char, Char, Char) -> String = { a, b, c, d, e -> "$e$d$c$b$a" }
    val p5_1 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), block = cb1_5)
    val p5_2 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), block = cb1_5)
    val p5_3 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), block = cb2_5)
    val p5_side = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), hasSideEffects = true, block = cb1_5)
    kotlin.test.assertTrue(p5_1.isEqualTo(p5_2))
    kotlin.test.assertFalse(p5_1.isEqualTo(p5_3))
    kotlin.test.assertFalse(p5_1.isEqualTo(p5_side))

    val cb1_6: (Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f -> "$a$b$c$d$e$f" }
    val cb2_6: (Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f -> "$f$e$d$c$b$a" }
    val p6_1 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), block = cb1_6)
    val p6_2 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), block = cb1_6)
    val p6_3 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), block = cb2_6)
    val p6_side = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), hasSideEffects = true, block = cb1_6)
    kotlin.test.assertTrue(p6_1.isEqualTo(p6_2))
    kotlin.test.assertFalse(p6_1.isEqualTo(p6_3))
    kotlin.test.assertFalse(p6_1.isEqualTo(p6_side))

    val cb1_7: (Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g -> "$a$b$c$d$e$f$g" }
    val cb2_7: (Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g -> "$g$f$e$d$c$b$a" }
    val p7_1 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), block = cb1_7)
    val p7_2 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), block = cb1_7)
    val p7_3 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), block = cb2_7)
    val p7_side = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), hasSideEffects = true, block = cb1_7)
    kotlin.test.assertTrue(p7_1.isEqualTo(p7_2))
    kotlin.test.assertFalse(p7_1.isEqualTo(p7_3))
    kotlin.test.assertFalse(p7_1.isEqualTo(p7_side))

    val cb1_8: (Char, Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g, h -> "$a$b$c$d$e$f$g$h" }
    val cb2_8: (Char, Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g, h -> "$h$g$f$e$d$c$b$a" }
    val p8_1 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), block = cb1_8)
    val p8_2 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), block = cb1_8)
    val p8_3 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), block = cb2_8)
    val p8_side = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), hasSideEffects = true, block = cb1_8)
    kotlin.test.assertTrue(p8_1.isEqualTo(p8_2))
    kotlin.test.assertFalse(p8_1.isEqualTo(p8_3))
    kotlin.test.assertFalse(p8_1.isEqualTo(p8_side))

    val cb1_9: (Char, Char, Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g, h, i -> "$a$b$c$d$e$f$g$h$i" }
    val cb2_9: (Char, Char, Char, Char, Char, Char, Char, Char, Char) -> String = { a, b, c, d, e, f, g, h, i -> "$i$h$g$f$e$d$c$b$a" }
    val p9_1 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'), block = cb1_9)
    val p9_2 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'), block = cb1_9)
    val p9_3 = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'), block = cb2_9)
    val p9_side = seqMap(char('1'), char('2'), char('3'), char('4'), char('5'), char('6'), char('7'), char('8'), char('9'), hasSideEffects = true, block = cb1_9)
    kotlin.test.assertTrue(p9_1.isEqualTo(p9_2))
    kotlin.test.assertFalse(p9_1.isEqualTo(p9_3))
    kotlin.test.assertFalse(p9_1.isEqualTo(p9_side))
  }
}
