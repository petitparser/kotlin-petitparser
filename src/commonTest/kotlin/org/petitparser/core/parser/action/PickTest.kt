package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.times
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PickTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().star().pick(-1))
  }

  @Test
  fun test_from_start() {
    val parser = (digit() seq letter()).pick(1)
    assertSuccess(parser, "1a", 'a')
    assertSuccess(parser, "2b", 'b')
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "12", "letter expected", 1)
  }

  @Test
  fun test_from_end() {
    val parser = (digit() seq letter()).pick(-1)
    assertSuccess(parser, "1a", 'a')
    assertSuccess(parser, "2b", 'b')
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "12", "letter expected", 1)
  }

  @Test
  fun test_slice_range() {
    val parser = digit().times(5).slice(1..3)
    assertSuccess(parser, "12345", listOf('2', '3', '4'))
    assertSuccess(parser, "67890", listOf('7', '8', '9'))
    assertFailure(parser, "abc", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_slice_indices() {
    val parser = digit().times(5).slice(listOf(3, 1, 2))
    assertSuccess(parser, "12345", listOf('4', '2', '3'))
    assertSuccess(parser, "67890", listOf('9', '7', '8'))
    assertFailure(parser, "abc", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_slice_vararg() {
    val parser = digit().times(5).slice(3, 1, 2)
    assertSuccess(parser, "12345", listOf('4', '2', '3'))
    assertSuccess(parser, "67890", listOf('9', '7', '8'))
  }

  @Test
  fun test_out_of_bounds() {
    val parser = (digit() seq letter()).pick(5)
    kotlin.test.assertFailsWith<IndexOutOfBoundsException> {
      parser.parse("1a").value
    }
  }

  @Test
  fun test_equality() {
    val p1 = any().star().pick(0)
    val p2 = any().star().pick(0)
    val p3 = any().star().pick(1)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(any()))
  }

  @Test
  fun test_to_string() {
    val parser = any().star().pick(2)
    assertEquals("PickParser[2]", parser.toString())
  }
}
