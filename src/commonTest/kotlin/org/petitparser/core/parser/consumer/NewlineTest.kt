package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class NewlineTest {
  @Test
  fun test_newline_default() {
    val parser = newline()
    expectParserInvariants(parser)
    assertSuccess(parser, "\n", "\n")
    assertSuccess(parser, "\r\n", "\r\n")
    assertSuccess(parser, "\r", "\r")
    assertSuccess(parser, "\nremaining", "\n", position = 1)
    assertSuccess(parser, "\r\nremaining", "\r\n", position = 2)
    assertSuccess(parser, "\rremaining", "\r", position = 1)
    assertFailure(parser, "", "newline expected")
    assertFailure(parser, "a", "newline expected")
    assertFailure(parser, "\b", "newline expected")
    assertEquals("NewlineParser[newline expected]", parser.toString())
  }

  @Test
  fun test_newline_custom_message() {
    val parser = newline("line break expected")
    expectParserInvariants(parser)
    assertSuccess(parser, "\n", "\n")
    assertFailure(parser, "", "line break expected")
    assertFailure(parser, "x", "line break expected")
    assertEquals("NewlineParser[line break expected]", parser.toString())
  }

  @Test
  fun test_newline_equality_and_copy() {
    val p1 = newline()
    val p2 = newline()
    val p3 = newline("other message")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))

    val copy = p1.copy()
    assertTrue(p1.isEqualTo(copy))
    assertEquals(p1.message, copy.message)
  }
}
