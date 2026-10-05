package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test

internal class CastTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().cast<String>())
  }

  @Test
  fun test_cast() {
    val parser = digit().map(Char::digitToInt).cast<Number>()
    assertSuccess(parser, "1", 1)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }



  @Test
  fun test_equality() {
    val p1 = digit().cast<Number>()
    val p2 = digit().cast<Number>()
    val p3 = digit().plus().cast<Number>()
    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
  }
}
