package org.petitparser.core.expression

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {
  @Test
  fun test_prefix_result() {
    val prefix = ExpressionResultPrefix<Int, String>("-") { op, v -> -v }
    assertEquals("-", prefix.operator)
    assertEquals(-5, prefix(5))
    assertEquals("ExpressionResultPrefix(-)", prefix.toString())
  }

  @Test
  fun test_postfix_result() {
    val postfix = ExpressionResultPostfix<Int, String>("++") { v, op -> v + 1 }
    assertEquals("++", postfix.operator)
    assertEquals(6, postfix(5))
    assertEquals("ExpressionResultPostfix(++)", postfix.toString())
  }

  @Test
  fun test_infix_result() {
    val infix = ExpressionResultInfix<Int, String>("+") { l, op, r -> l + r }
    assertEquals("+", infix.operator)
    assertEquals(8, infix(3, 5))
    assertEquals("ExpressionResultInfix(+)", infix.toString())
  }
}
