package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.times
import org.petitparser.core.parser.utils.Tuple2
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.consumer.letter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FlattenTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().flatten())
    expectParserInvariants(any().flatten(message = "custom"))
  }

  @Test
  fun test_default() {
    val parser = digit().times(2).flatten()
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "1", "digit expected", 1)
    assertFailure(parser, "1a", "digit expected", 1)
    assertSuccess(parser, "12", "12")
    assertSuccess(parser, "123", "12", 2)
  }

  @Test
  fun test_with_message() {
    val parser = digit().times(2).flatten(message = "gimme a number")
    assertFailure(parser, "", "gimme a number", 0)
    assertFailure(parser, "a", "gimme a number", 0)
    assertFailure(parser, "1", "gimme a number", 0)
    assertFailure(parser, "1a", "gimme a number", 0)
    assertSuccess(parser, "12", "12")
    assertSuccess(parser, "123", "12", 2)
  }

  @Test
  fun test_nested() {
    val parser = digit().plus().flatten().plus().flatten()
    assertSuccess(parser, "1", "1")
    assertSuccess(parser, "112", "112")
  }

  @Test
  fun test_empty_matching() {
    val p1 = digit().star().flatten()
    assertSuccess(p1, "", "")
    assertSuccess(p1, "123", "123")

    val p2 = digit().star().flatten(message = "digits")
    assertSuccess(p2, "", "")
    assertSuccess(p2, "123", "123")
  }

  @Test
  fun test_offset() {
    val parser = letter() seq digit().plus().flatten()
    assertSuccess(parser, "a123", listOf('a', "123"))
  }

  @Test
  fun test_equality() {
    val p1 = digit().flatten()
    val p2 = digit().flatten()
    val p3 = digit().flatten(message = "msg")
    val p4 = digit().flatten(message = "msg")
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertTrue(p3.isEqualTo(p4))
    assertFalse(p1.isEqualTo(digit()))
  }

  @Test
  fun test_to_string() {
    val p1 = digit().flatten()
    val p2 = digit().flatten("custom")
    assertEquals("FlattenParser", p1.toString())
    assertEquals("FlattenParser[custom]", p2.toString())
  }
}
