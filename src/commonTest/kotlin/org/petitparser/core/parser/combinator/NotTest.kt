package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import kotlin.test.Test

class NotTest {
  @Test
  fun test_not() {
    val parser = digit().not("no digit expected")
    expectParserInvariants(parser)
    assertFailure(parser, "1", "no digit expected", 0)
    val result = parser.parse("a")
    assertSuccess(result, result.value, 0)
    assertFailure<Char>(result.value, "digit expected", 0)
  }

  @Test
  fun test_char_neg() {
    val parser = digit().neg("no digit expected")
    expectParserInvariants(parser)
    assertFailure(parser, "1", "no digit expected", 0)
    assertFailure(parser, "9", "no digit expected", 0)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "", "input expected", 0)
  }

  @Test
  fun test_equality() {
    val p1 = digit().not("msg1")
    val p2 = digit().not("msg1")
    val p3 = digit().not("msg2")
    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
    kotlin.test.assertFalse(p1.isEqualTo(digit()))
  }
}
