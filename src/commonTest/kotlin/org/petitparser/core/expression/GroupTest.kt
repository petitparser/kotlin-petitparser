package org.petitparser.core.expression

import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GroupTest {
  @Test
  fun test_group_primitives_empty_fails() {
    val loopback = undefined<Int>()
    val group = ExpressionGroup(loopback)
    assertFailsWith<IllegalStateException> {
      group.build(null)
    }
  }

  @Test
  fun test_group_duplicate_optional_fails() {
    val loopback = undefined<Int>()
    val group = ExpressionGroup(loopback)
    group.optional(0)
    assertFailsWith<IllegalStateException> {
      group.optional(1)
    }
  }

  @Test
  fun test_build_choice() {
    val p1 = char('a')
    val p2 = char('b')
    assertEquals(p1, ExpressionGroup.buildChoice(listOf(p1)))
    val choice = ExpressionGroup.buildChoice(listOf(p1, p2))
    assertEquals(2, choice.children.size)
  }
}
