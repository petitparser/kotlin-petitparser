package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EpsilonTest {
  @Suppress("DEPRECATION")
  @Test
  fun test_invariants() {
    expectParserInvariants(epsilon())
    expectParserInvariants(epsilon(42))
    expectParserInvariants(epsilonWith("hello"))
    expectParserInvariants(success())
    expectParserInvariants(success(true))
    expectParserInvariants(epsilonWith<String?>(null))
  }

  @Test
  fun test_default_unit() {
    val parser = epsilon()
    assertSuccess(parser, "", Unit, 0)
    assertSuccess(parser, "a", Unit, 0)
    assertSuccess(parser, "abc", Unit, 0)
    assertEquals(Unit, parser.value)
    assertEquals(Unit, parser.result)
  }

  @Test
  fun test_epsilon_with_value() {
    val p1 = epsilon(42)
    assertSuccess(p1, "", 42, 0)
    assertSuccess(p1, "abc", 42, 0)
    assertEquals(42, p1.value)
    assertEquals(42, p1.result)

    val p2 = epsilonWith("foo")
    assertSuccess(p2, "", "foo", 0)
    assertSuccess(p2, "xyz", "foo", 0)
    assertEquals("foo", p2.value)
    assertEquals("foo", p2.result)
  }

  @Suppress("DEPRECATION")
  @Test
  fun test_success_aliases() {
    val s1 = success()
    assertSuccess(s1, "", Unit, 0)
    assertSuccess(s1, "a", Unit, 0)

    val s2 = success(123)
    assertSuccess(s2, "", 123, 0)
    assertSuccess(s2, "abc", 123, 0)
  }

  @Test
  fun test_epsilon_at_offset() {
    val input = org.petitparser.core.context.Input.Impl("hello", 3)
    val result = epsilon(100).parseOn(input)
    assertSuccess(result, 100, 3)
    assertEquals(3, epsilon(100).fastParseOn("hello", 3))
  }

  @Test
  fun test_nullable_value() {
    val p = epsilonWith<String?>(null)
    assertSuccess(p, "", null, 0)
    assertSuccess(p, "a", null, 0)
    assertNull(p.value)
    assertNull(p.result)
  }

  @Test
  fun test_equality() {
    val p1 = epsilon(42)
    val p2 = epsilon(42)
    val p3 = epsilon(43)
    val p4 = epsilon("42")
    val p5 = epsilonWith<String?>(null)
    val p6 = epsilonWith<String?>(null)

    assertTrue(p1.isEqualTo(p2))
    assertTrue(p5.isEqualTo(p6))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(p4))
    assertFalse(p1.isEqualTo(p5))
    assertFalse(p1.isEqualTo(any()))
    assertFalse(p1.isEqualTo(null))
  }

  @Test
  fun test_to_string() {
    assertEquals("EpsilonParser[kotlin.Unit]", epsilon().toString())
    assertEquals("EpsilonParser[42]", epsilon(42).toString())
    assertEquals("EpsilonParser[null]", epsilonWith<String?>(null).toString())
  }

  @Test
  fun test_epsilon_in_sequence() {
    val parser = char('a') + epsilon("middle") + char('b')
    assertSuccess(parser, "ab", listOf('a', "middle", 'b'), 2)
    assertEquals(2, parser.fastParseOn("ab", 0))
  }

  @Test
  fun test_has_equal_properties() {
    val p1 = epsilon(10)
    val p2 = epsilon(10)
    val p3 = epsilon(20)
    assertTrue(p1.hasEqualProperties(p2))
    assertFalse(p1.hasEqualProperties(p3))
    assertFalse(p1.hasEqualProperties(any()))
  }
}
