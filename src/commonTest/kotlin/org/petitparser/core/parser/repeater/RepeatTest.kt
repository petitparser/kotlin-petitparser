package org.petitparser.core.parser.repeater

import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RepeatTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(digit().star())
    expectParserInvariants(digit().plus())
    expectParserInvariants(digit().times(2))
    expectParserInvariants(digit().repeat(2, 4))
  }

  @Test
  fun test_repeat_star() {
    val parser = digit().star()
    assertSuccess(parser, "", listOf())
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
  }

  @Test
  fun test_repeat_plus() {
    val parser = digit().plus()
    assertFailure(parser, "", "digit expected", 0)
    assertSuccess(parser, "1", listOf('1'))
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3', '4'))
  }

  @Test
  fun test_repeat_times() {
    val parser = digit().times(3)
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "1", "digit expected", 1)
    assertFailure(parser, "12", "digit expected", 2)
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3'), 3)
  }

  @Test
  fun test_repeat_repeat() {
    val parser = digit().repeat(2, 3)
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "1", "digit expected", 1)
    assertSuccess(parser, "12", listOf('1', '2'))
    assertSuccess(parser, "123", listOf('1', '2', '3'))
    assertSuccess(parser, "1234", listOf('1', '2', '3'), 3)
  }

  @Test
  fun test_repeat_default_max() {
    val parser = digit().repeat(2)
    assertEquals(2, parser.min)
    assertEquals(2, parser.max)
  }

  @Test
  fun test_repeat_times_zero() {
    val parser = digit().times(0)
    assertSuccess(parser, "", listOf())
    assertSuccess(parser, "1", listOf(), 0)
  }

  @Test
  fun test_repeat_times_negative() {
    val error = assertFailsWith<IllegalArgumentException> {
      digit().times(-1)
    }
    assertEquals("min must be at least 0, but got -1", error.message)
  }

  @Test
  fun test_repeat_large() {
    val count = 10_000
    val input = "a".repeat(count)
    val parser = char('a').repeat(2, Int.MAX_VALUE)
    val expected = List(count) { 'a' }
    assertSuccess(parser, input, expected)
  }

  @Test
  fun test_infinite_loop_protection() {
    val emptyParser = Parser { it.success(Unit) }
    val starParser = emptyParser.star()
    val error1 = assertFailsWith<IllegalStateException> {
      starParser.parseOn(org.petitparser.core.context.Input.Impl("", 0))
    }
    assertEquals("$emptyParser must always consume", error1.message)

    val error2 = assertFailsWith<IllegalStateException> {
      starParser.fastParseOn("", 0)
    }
    assertEquals("$emptyParser must always consume", error2.message)

    val plusParser = emptyParser.plus()
    val error3 = assertFailsWith<IllegalStateException> {
      plusParser.parseOn(org.petitparser.core.context.Input.Impl("", 0))
    }
    assertEquals("$emptyParser must always consume", error3.message)

    val error4 = assertFailsWith<IllegalStateException> {
      plusParser.fastParseOn("", 0)
    }
    assertEquals("$emptyParser must always consume", error4.message)
  }
}
