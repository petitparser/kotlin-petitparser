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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PermuteTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().star().permute(-1, 1))
  }

  @Test
  fun test_from_start() {
    val parser = (digit() seq letter()).permute(1, 0)
    assertSuccess(parser, "1a", listOf('a', '1'))
    assertSuccess(parser, "2b", listOf('b', '2'))
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "12", "letter expected", 1)
  }

  @Test
  fun test_from_end() {
    val parser = (digit() seq letter()).permute(-1, 0)
    assertSuccess(parser, "1a", listOf('a', '1'))
    assertSuccess(parser, "2b", listOf('b', '2'))
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "12", "letter expected", 1)
  }

  @Test
  fun test_repeated() {
    val parser = (digit() seq letter()).permute(1, 1)
    assertSuccess(parser, "1a", listOf('a', 'a'))
    assertSuccess(parser, "2b", listOf('b', 'b'))
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "12", "letter expected", 1)
  }

  @Test
  fun test_list_overload() {
    val parser = (digit() seq letter()).permute(listOf(1, 0))
    assertSuccess(parser, "1a", listOf('a', '1'))
  }

  @Test
  fun test_iterable_overload() {
    val parser = (digit() seq letter()).permute(0..1)
    assertSuccess(parser, "1a", listOf('1', 'a'))
  }

  @Test
  fun test_empty_indices() {
    val parser = (digit() seq letter()).permute()
    assertSuccess(parser, "1a", emptyList<Char>())
  }

  @Test
  fun test_out_of_bounds() {
    val parser = (digit() seq letter()).permute(2)
    kotlin.test.assertFailsWith<IndexOutOfBoundsException> {
      parser.parse("1a").value
    }
  }

  @Test
  fun test_equality() {
    val p1 = any().star().permute(0, 1)
    val p2 = any().star().permute(0, 1)
    val p3 = any().star().permute(1, 0)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
  }

  @Test
  fun test_to_string() {
    val parser = any().star().permute(1, 0)
    assertEquals("PermuteParser[1, 0]", parser.toString())
  }
}
