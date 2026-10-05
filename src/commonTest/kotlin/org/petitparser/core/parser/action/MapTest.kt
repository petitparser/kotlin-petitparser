package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MapTest {
  @Test
  fun test_invariants() {
    val fn: (Char) -> Char = { it }
    expectParserInvariants(any().map(fn))
    expectParserInvariants(any().map(hasSideEffects = true, callback = fn))
  }

  @Test
  fun test_map() {
    val parser = digit().map(Char::digitToInt)
    assertSuccess(parser, "0", 0)
    assertSuccess(parser, "5", 5)
    assertSuccess(parser, "9", 9)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_without_side_effects() {
    val effects = mutableListOf<Char>()
    val parser = digit().map { effects.add(it); it }
    assertEquals(1, parser.fastParseOn("1", 0))
    assertTrue(effects.isEmpty())
  }

  @Test
  fun test_with_side_effects() {
    val effects = mutableListOf<Char>()
    val parser = digit().map(hasSideEffects = true) { effects.add(it); it }
    assertEquals(1, parser.fastParseOn("1", 0))
    assertEquals(listOf('1'), effects)

    val effects2 = mutableListOf<Char>()
    val parser2 = digit().map(true) { effects2.add(it); it }
    assertEquals(1, parser2.fastParseOn("1", 0))
    assertEquals(listOf('1'), effects2)
  }

  @Test
  fun test_equality() {
    val fn1: (Char) -> Char = { it }
    val fn2: (Char) -> Char = { it.uppercaseChar() }
    val p1 = digit().map(fn1)
    val p2 = digit().map(fn1)
    val p3 = digit().map(fn2)
    val p4 = digit().map(hasSideEffects = true, callback = fn1)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(p4))
  }
}
