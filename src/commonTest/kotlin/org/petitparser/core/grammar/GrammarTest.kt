package org.petitparser.core.grammar

import org.petitparser.core.definition.resolve
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GrammarTest {
  private class RecursiveGrammar : Grammar() {
    val element: Parser<String> by digit().plus().flatten()
    val list: Parser<Any?> by seqMap(ref(::element), char(','), ref(::list)) { first, comma, rest ->
      listOf(first, comma, rest)
    } / ref(::element)
    val start: Parser<Any?> by ref(::list).end()
  }

  @Test
  fun test_grammar_properties() {
    val grammar = RecursiveGrammar()
    assertEquals(setOf("element", "list", "start"), grammar.parserNames)
    assertEquals(grammar.element, grammar["element"])
    assertEquals(grammar.list, grammar["list"])
    assertEquals(grammar.start, grammar["start"])
  }

  @Test
  fun test_grammar_parsing() {
    val grammar = RecursiveGrammar()
    assertSuccess(grammar.start, "1", "1")
    assertSuccess(grammar.start, "1,2", listOf("1", ',', "2"))
    assertSuccess(grammar.start, "1,2,3", listOf("1", ',', listOf("2", ',', "3")))
  }

  @Test
  fun test_grammar_resolution() {
    val grammar = RecursiveGrammar()
    val resolved = resolve(grammar.start)
    assertSuccess(resolved, "1", "1")
    assertSuccess(resolved, "1,2", listOf("1", ',', "2"))
    assertSuccess(resolved, "1,2,3", listOf("1", ',', listOf("2", ',', "3")))
  }

  @Test
  fun test_grammar_ref_methods() {
    var called = false
    val grammar = object : Grammar() {
      val r = ref {
        called = true
        char('a')
      }
    }
    assertEquals(grammar.r, grammar.r.copy())
    assertEquals(1, grammar.r.fastParseOn("a", 0))
    assertTrue(called)
  }
}
