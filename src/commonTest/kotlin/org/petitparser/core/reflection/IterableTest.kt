package org.petitparser.core.reflection

import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals

class IterableTest {
  @Test
  fun test_single() {
    val parser = char('a')
    assertEquals(listOf(parser), allParsers(parser).toList())
  }

  @Test
  fun test_chain() {
    val leaf = char('a')
    val settable = leaf.settable()
    assertEquals(listOf(settable, leaf), allParsers(settable).toList())
  }

  @Test
  fun test_branching() {
    val p1 = char('a')
    val p2 = char('b')
    val choice = p1 or p2
    assertEquals(listOf(choice, p1, p2), allParsers(choice).toList())
  }

  @Test
  fun test_cycle() {
    val settable = undefined<Char>()
    val choice = char('a') or settable
    settable.set(choice)
    val list = allParsers(settable).toList()
    assertEquals(3, list.size)
    assertEquals(setOf(settable, choice, choice.children[0]), list.toSet())
  }
}
