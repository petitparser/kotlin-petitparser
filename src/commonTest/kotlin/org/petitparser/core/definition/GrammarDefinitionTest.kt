package org.petitparser.core.definition

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GrammarDefinitionTest {
  private class SimpleGrammar : GrammarDefinition<Char>() {
    val a: Parser<Char> by def { char('a') }
    override fun start(): Parser<Char> = a
  }

  @Test
  fun test_grammar_definition_build() {
    val grammar = SimpleGrammar()
    assertEquals(setOf("a"), grammar.productionNames)
    val parser = grammar.build()
    assertSuccess(parser, "a", 'a')
  }

  @Test
  fun test_missing_start_throws() {
    val empty = object : GrammarDefinition<Unit>() {}
    assertFailsWith<UnsupportedOperationException> {
      empty.start()
    }
  }
}
