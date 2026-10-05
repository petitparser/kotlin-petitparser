package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ConstantTest {
  @Test
  fun test_invariants() {
    val parser = digit().constant(42)
    expectParserInvariants(parser)
  }

  @Test
  fun test_constant() {
    val parser = digit().constant(42)
    assertSuccess(parser, "1", 42)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_constant_null() {
    val parser = digit().constant<String?>(null)
    assertSuccess(parser, "1", null)
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_constant_string() {
    val parser = digit().constant("constant")
    assertSuccess(parser, "1", "constant")
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_equality() {
    val p1 = digit().constant(42)
    val p2 = digit().constant(42)
    val p3 = digit().constant(43)
    val p4 = digit().constant<Int?>(null)
    val p5 = digit().constant<Int?>(null)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertTrue(p4.isEqualTo(p5))
    assertFalse(p1.isEqualTo(p4))
  }
}
