package org.petitparser.core.expression

import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.digit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BuilderTest {
  @Test
  fun test_dsl_build_expression() {
    val parser = buildExpression<Int> {
      primitive(digit().map { it.digitToInt() })
      group().left(org.petitparser.core.parser.consumer.char('+')) { a, _, b -> a + b }
    }
    assertSuccess(parser, "1+2+3", 6)
  }

  @Test
  fun test_empty_builder_fails() {
    val builder = ExpressionBuilder<Int>()
    assertFailsWith<IllegalStateException> {
      builder.build()
    }
  }
}
