package org.petitparser.core.parser.misc

import org.petitparser.core.parser.action.pick
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PositionTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(position())
  }

  @Test
  fun test_position() {
    val parser = position()
    assertSuccess(parser, "", 0, 0)
    assertSuccess(parser, "a", 0, 0)
    assertSuccess(parser, "abc", 0, 0)
  }

  @Test
  fun test_position_in_sequence() {
    val parser = (any().star() + position()).pick(-1)
    assertSuccess(parser, "", 0)
    assertSuccess(parser, "a", 1)
    assertSuccess(parser, "aa", 2)
    assertSuccess(parser, "aaa", 3)
  }

  @Test
  fun test_equality() {
    val p1 = position()
    val p2 = position()

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(any()))
    assertFalse(p1.isEqualTo(null))
  }

  @Test
  fun test_position_at_offset() {
    val input = org.petitparser.core.context.Input.Impl("hello", 3)
    val result = position().parseOn(input)
    assertSuccess(result, 3, 3)
    assertEquals(3, position().fastParseOn("hello", 3))
  }

  @Test
  fun test_to_string() {
    assertEquals("PositionParser", position().toString())
  }

  @Test
  fun test_position_between_tokens() {
    val parser = char('a') + position() + char('b')
    assertSuccess(parser, "ab", listOf('a', 1, 'b'), 2)
    assertEquals(2, parser.fastParseOn("ab", 0))
  }

  @Test
  fun test_has_equal_properties() {
    val p1 = position()
    val p2 = position()
    assertTrue(p1.hasEqualProperties(p2))
  }
}
