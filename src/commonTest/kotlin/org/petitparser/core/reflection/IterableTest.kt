package org.petitparser.core.reflection

import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.lowercase
import org.petitparser.core.parser.consumer.uppercase
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IterableTest {
  @Test
  fun test_allParsers_single() {
    val parser = lowercase()
    assertEquals(listOf(parser), allParsers(parser).toList())
  }

  @Test
  fun test_allParsers_chain() {
    val leaf = char('a')
    val settable = leaf.settable()
    assertEquals(listOf(settable, leaf), allParsers(settable).toList())
  }

  @Test
  fun test_allParsers_nested() {
    val p3 = lowercase()
    val p2 = p3.star()
    val p1 = p2.flatten()
    assertEquals(listOf(p1, p2, p3), allParsers(p1).toList())
  }

  @Test
  fun test_allParsers_branched() {
    val p3 = lowercase()
    val p2 = uppercase()
    val p1 = p2.seq(p3)
    assertEquals(listOf(p1, p2, p3), allParsers(p1).toList())
  }

  @Test
  fun test_allParsers_duplicated() {
    val p2 = uppercase()
    val p1 = p2.seq(p2)
    assertEquals(listOf(p1, p2), allParsers(p1).toList())
  }

  @Test
  fun test_allParsers_knot() {
    val p1 = undefined<Unit>()
    p1.set(p1)
    assertEquals(listOf(p1), allParsers(p1).toList())
  }

  @Test
  fun test_allParsers_looping() {
    val p1 = undefined<Unit>()
    val p2 = undefined<Unit>()
    val p3 = undefined<Unit>()
    p1.set(p2)
    p2.set(p3)
    p3.set(p1)
    assertEquals(listOf(p1, p2, p3), allParsers(p1).toList())
  }

  @Test
  fun test_allParsers_cycle() {
    val settable = undefined<Char>()
    val choice = char('a') or settable
    settable.set(choice)
    val list = allParsers(settable).toList()
    assertEquals(3, list.size)
    assertEquals(setOf(settable, choice, choice.children[0]), list.toSet())
  }

  @Test
  fun test_allChildren_single() {
    val inner = char('a')
    val parser = inner.plus()
    assertEquals(setOf(inner), allChildren(parser).toSet())
    assertTrue(allChildren(inner).toList().isEmpty())
  }

  @Test
  fun test_allChildren_multiple() {
    val inner1 = char('a')
    val inner2 = char('b')
    val parser = inner1.seq(inner2)
    assertEquals(setOf(inner1, inner2), allChildren(parser).toSet())
    assertTrue(allChildren(inner1).toList().isEmpty())
    assertTrue(allChildren(inner2).toList().isEmpty())
  }

  @Test
  fun test_allChildren_repeated() {
    val inner1 = char('a')
    val inner2 = char('b')
    val parser = inner1 or inner2 or inner2
    assertEquals(setOf(inner1, inner2), allChildren(parser).toSet())
    assertTrue(allChildren(inner1).toList().isEmpty())
    assertTrue(allChildren(inner2).toList().isEmpty())
  }

  @Test
  fun test_allChildren_recursive() {
    val inner1 = char('a')
    val inner2 = undefined<Char>()
    val parser = listOf(inner1, inner2).toChoiceParser()
    inner2.set(parser)
    assertEquals(setOf(inner1, inner2, parser), allChildren(parser).toSet())
    assertTrue(allChildren(inner1).toList().isEmpty())
    assertEquals(setOf(inner1, inner2, parser), allChildren(inner2).toSet())
  }

  @Test
  fun test_allChildren_selfReference() {
    val parser = undefined<Unit>()
    parser.set(parser)
    assertEquals(setOf(parser), allChildren(parser).toSet())
  }
}
