package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EndOfInputTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(endOfInput())
    expectParserInvariants(endOfInput("custom end"))
  }

  @Test
  fun test_endOfInput_default() {
    val parser = endOfInput()
    assertSuccess(parser, "", Unit, 0)
    assertFailure(parser, "1", "end of input expected", 0)
    assertFailure(parser, "123", "end of input expected", 0)
  }

  @Test
  fun test_endOfInput_custom_message() {
    val parser = endOfInput("custom error")
    assertEquals("custom error", parser.message)
    assertSuccess(parser, "", Unit, 0)
    assertFailure(parser, "a", "custom error", 0)
    assertFailure(parser, "abc", "custom error", 0)
  }

  @Test
  fun test_end_extension() {
    val parser = any().end()
    assertSuccess(parser, "a", 'a', 1)
    assertFailure(parser, "aa", "end of input expected", 1)
    assertFailure(parser, "", "input expected", 0)
  }

  @Test
  fun test_end_extension_custom_message() {
    val parser = char('x').end("custom end expected")
    assertSuccess(parser, "x", 'x', 1)
    assertFailure(parser, "xy", "custom end expected", 1)
    assertFailure(parser, "y", "'x' expected", 0)
    assertFailure(parser, "", "'x' expected", 0)
  }

  @Test
  fun test_equality() {
    val p1 = endOfInput()
    val p2 = endOfInput()
    val p3 = endOfInput("custom")
    val p4 = endOfInput("custom")

    assertTrue(p1.isEqualTo(p2))
    assertTrue(p3.isEqualTo(p4))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(any()))
    assertFalse(p1.isEqualTo(null))
  }

  @Test
  fun test_end_at_offset() {
    val parser = endOfInput()
    val inputSuccess = org.petitparser.core.context.Input.Impl("hello", 5)
    assertSuccess(parser.parseOn(inputSuccess), Unit, 5)
    assertEquals(5, parser.fastParseOn("hello", 5))

    val inputFailure = org.petitparser.core.context.Input.Impl("hello", 3)
    assertFailure(parser.parseOn(inputFailure), "end of input expected", 3)
    assertEquals(-1, parser.fastParseOn("hello", 3))
  }

  @Test
  fun test_to_string() {
    assertEquals("EndOfInputParser[end of input expected]", endOfInput().toString())
    assertEquals("EndOfInputParser[custom]", endOfInput("custom").toString())
  }

  @Test
  fun test_endOfInput_in_choice() {
    val parser = char('a') or endOfInput()
    assertSuccess(parser, "a", 'a', 1)
    assertSuccess(parser, "", Unit, 0)
    assertFailure(parser, "b", "end of input expected", 0)
  }

  @Test
  fun test_has_equal_properties() {
    val p1 = endOfInput("err")
    val p2 = endOfInput("err")
    val p3 = endOfInput("diff")
    assertTrue(p1.hasEqualProperties(p2))
    assertFalse(p1.hasEqualProperties(p3))
    assertFalse(p1.hasEqualProperties(any()))
  }
}
