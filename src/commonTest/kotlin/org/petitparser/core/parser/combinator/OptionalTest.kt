package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test

class OptionalTest {
  @Test
  fun test_optional() {
    val parser: OptionalParser<Char?> = digit().optional()
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "a", null, 0)
    assertSuccess(parser, "", null, 0)
  }

  @Test
  fun test_optional_with_fallback() {
    val parser: OptionalParser<Char> = digit().optional('*')
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "a", '*', 0)
    assertSuccess(parser, "", '*', 0)
  }

  @Test
  fun test_optionalWith() {
    val parser: OptionalParser<Char> = digit().optionalWith('*')
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "a", '*', 0)
    assertSuccess(parser, "", '*', 0)
  }

  @Test
  fun test_equality() {
    val p1 = digit().optional('*')
    val p2 = digit().optional('*')
    val p3 = digit().optional('-')
    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
    kotlin.test.assertFalse(p1.isEqualTo(digit()))
  }
}
