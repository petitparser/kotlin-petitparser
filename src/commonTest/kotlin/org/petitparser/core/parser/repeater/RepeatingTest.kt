package org.petitparser.core.parser.repeater

import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepeatingTest {
  @Test
  fun test_bounds_validation() {
    val error1 = assertFailsWith<IllegalArgumentException> {
      char('a').repeat(-1, 2)
    }
    assertEquals("min must be at least 0, but got -1", error1.message)

    val error2 = assertFailsWith<IllegalArgumentException> {
      char('a').repeat(3, 2)
    }
    assertEquals("max must be at least 3, but got 2", error2.message)
  }

  @Test
  fun test_repeating_toString() {
    assertEquals("PossessiveRepeatingParser[0..*]", char('a').star().toString())
    assertEquals("PossessiveRepeatingParser[1..*]", char('a').plus().toString())
    assertEquals("PossessiveRepeatingParser[2..2]", char('a').times(2).toString())
    assertEquals("PossessiveRepeatingParser[2..5]", char('a').repeat(2, 5).toString())
  }

  @Test
  fun test_repeating_equality() {
    val p1 = char('a').repeat(1, 2)
    val p2 = char('a').repeat(1, 2)
    val pDiffMin = char('a').repeat(0, 2)
    val pDiffMax = char('a').repeat(1, 3)

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(pDiffMin))
    assertFalse(p1.isEqualTo(pDiffMax))
  }

  @Test
  fun test_limited_repeating_parser_children_and_replace() {
    val delegate = char('a')
    val limit = char('b')
    val parser = delegate.starGreedy(limit)

    assertEquals(listOf(delegate, limit), parser.children)

    val newDelegate = char('c')
    parser.replace(delegate, newDelegate)
    assertEquals(newDelegate, parser.delegate)
    assertEquals(listOf(newDelegate, limit), parser.children)

    val newLimit = char('d')
    parser.replace(limit, newLimit)
    assertEquals(newLimit, parser.limit)
    assertEquals(listOf(newDelegate, newLimit), parser.children)
  }

  @Test
  fun test_limited_repeating_parser_invariants() {
    val parser = char('a').repeatGreedy(char('b'), 1, 3)
    expectParserInvariants(parser)
  }
}
