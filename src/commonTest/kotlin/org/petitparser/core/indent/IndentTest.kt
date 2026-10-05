package org.petitparser.core.indent

import org.petitparser.core.context.Output
import org.petitparser.core.definition.GrammarDefinition
import org.petitparser.core.definition.def
import org.petitparser.core.definition.ref
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.accept
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.utils.Tuple2
import org.petitparser.core.parser.combinator.neg
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.misc.endOfInput
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.plusString
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.reflection.linter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IndentList : GrammarDefinition<List<Any?>>() {
  val indent = Indent()

  val start: Parser<List<Any?>> by def {
    seqMap(
      ref(::newlines).optional(),
      ref(::things).optional(),
      ref(::newlines).optional(),
      endOfInput(),
    ) { _, things, _, _ -> things ?: emptyList() }
  }

  val things: Parser<List<Any?>> by def {
    seqMap(
      indent.same,
      ref(::obj) or ref(::line),
    ) { _, value -> value }.plus()
  }

  val obj: Parser<Map<String, Any?>> by def {
    seqMap(
      ref(::key),
      ref(::block) or ref(::inline),
    ) { key, values -> mapOf(key to values) }
  }

  val key: Parser<String> by def {
    seqMap(
      pattern("^ \t\r\n:").plusString(),
      indent.parser.star(),
      char(':'),
      indent.parser.star(),
    ) { key, _, _, _ -> key }
  }

  val block: Parser<List<Any?>> by def {
    seqMap(
      ref(::newlines),
      indent.during(ref(::things)),
    ) { _, things -> things }
  }

  val inline: Parser<String> by def { ref(::line) }

  val line: Parser<String> by def {
    seqMap(
      newline().neg().plus().flatten(),
      ref(::newlines).optional(),
    ) { line, _ -> line }
  }

  val whitespaces: Parser<List<Char>> by def { indent.parser.star() }

  val newlineParser: Parser<String> by def { newline() }

  val newlines: Parser<List<Tuple2<List<Char>, String>>> by def {
    seq(ref(::whitespaces), ref(::newlineParser)).plus()
  }
}

class IndentTest {
  @Test
  fun test_during_success_restores_indentation() {
    val indent = Indent()
    val inner = seqMap(indent.same, char('a')) { ind, ch -> "$ind$ch" }
    val parser = indent.during(inner)

    val result = parser.parse(" a")
    assertTrue(result is Output.Success)
    assertEquals(" a", result.value)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_during_failure_rolls_back_indentation() {
    val indent = Indent()
    val inner = seq(indent.same, char('a'))
    val parser = indent.during(inner)

    val result = parser.parse(" b")
    assertTrue(result is Output.Failure)
    assertEquals(1, result.position)
    assertEquals("'a' expected", result.message)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_nested_failure_rolls_back_each_level() {
    val indent = Indent()
    val inner = indent.during(seq(indent.same, char('b')))
    val outer = indent.during(seq(indent.same, char('\n'), inner))

    val result = outer.parse(" \n  c")
    assertTrue(result is Output.Failure)
    assertEquals(4, result.position)
    assertEquals("'b' expected", result.message)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_choice_rollback_allows_alternate_path() {
    val indent = Indent()
    val inner1 = seq(indent.same, char('a'))
    val inner2 = seq(indent.same, char('b'))
    val parser = listOf(indent.during(inner1), indent.during(inner2)).toChoiceParser()

    val result = parser.parse(" b")
    assertTrue(result is Output.Success)
    assertEquals(Tuple2(" ", 'b'), result.value)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_during_linter() {
    val indent = Indent()
    val parser = indent.during(seq(indent.same, char('a')))
    assertTrue(linter(parser).isEmpty())
  }

  @Test
  fun test_increase_failure_leaves_state_unchanged() {
    val indent = Indent()
    val inner = seq(indent.same, char('a'))
    val parser = indent.during(inner)

    val result = parser.parse("a")
    assertTrue(result is Output.Failure)
    assertEquals(0, result.position)
    assertEquals("indented expected", result.message)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_restores_non_empty_parent_indentation_on_success() {
    val indent = Indent()
    val block = indent.during(seq(indent.same, char('b')))
    val parser = indent.during(seq(indent.same, char('a'), block))

    val result = parser.parse("  a   b")
    assertTrue(result is Output.Success)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_consecutive_during_blocks_at_same_level() {
    val indent = Indent()
    val block1 = indent.during(seq(indent.same, char('a')))
    val block2 = indent.during(seq(indent.same, char('b')))
    val parser = seq(block1, block2)

    val result = parser.parse(" a b")
    assertTrue(result is Output.Success)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_accept_fast_parse_on_success_and_failure() {
    val indent = Indent()
    val inner = seq(indent.same, char('a'))
    val parser = indent.during(inner)

    assertTrue(parser.accept(" a"))
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())

    assertFalse(parser.accept(" b"))
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }

  @Test
  fun test_grammar_definition_empty() {
    val definition = IndentList()
    val parser = definition.build()

    val r1 = parser.parse("")
    assertTrue(r1 is Output.Success)
    assertEquals(emptyList<Any?>(), r1.value)
    assertTrue(definition.indent.stack.isEmpty())
    assertEquals("", definition.indent.current)

    val r2 = parser.parse("\n")
    assertTrue(r2 is Output.Success)
    assertEquals(emptyList<Any?>(), r2.value)

    val r3 = parser.parse("\r\n")
    assertTrue(r3 is Output.Success)
    assertEquals(emptyList<Any?>(), r3.value)
  }

  @Test
  fun test_grammar_definition_single_and_same_indent() {
    val definition = IndentList()
    val parser = definition.build()

    val r1 = parser.parse("a:\n b")
    assertTrue(r1 is Output.Success)
    assertEquals(listOf(mapOf("a" to listOf("b"))), r1.value)
    assertTrue(definition.indent.stack.isEmpty())
    assertEquals("", definition.indent.current)

    val r2 = parser.parse("a:\n b\n c")
    assertTrue(r2 is Output.Success)
    assertEquals(listOf(mapOf("a" to listOf("b", "c"))), r2.value)
    assertTrue(definition.indent.stack.isEmpty())
    assertEquals("", definition.indent.current)
  }

  @Test
  fun test_grammar_definition_nested_indent() {
    val definition = IndentList()
    val parser = definition.build()

    val r = parser.parse("a:\n  b:\n    c")
    assertTrue(r is Output.Success)
    assertEquals(
      listOf(mapOf("a" to listOf(mapOf("b" to listOf("c"))))),
      r.value,
    )
    assertTrue(definition.indent.stack.isEmpty())
    assertEquals("", definition.indent.current)
  }

  @Test
  fun test_grammar_definition_mismatched_indent() {
    val definition = IndentList()
    val parser = definition.build()

    val r = parser.parse("a:\n b\n\tc")
    assertTrue(r is Output.Failure)
    assertEquals(6, r.position)
    assertTrue(definition.indent.stack.isEmpty())
    assertEquals("", definition.indent.current)
  }

  @Suppress("DEPRECATION")
  @Test
  fun test_deprecated_increase_decrease() {
    val indent = Indent()
    val inc = indent.increase
    val dec = indent.decrease

    val rInc = inc.parse("  ")
    assertTrue(rInc is Output.Success)
    assertEquals("  ", indent.current)
    assertEquals(listOf(""), indent.stack)

    val rDec = dec.parse("")
    assertTrue(rDec is Output.Success)
    assertEquals("", indent.current)
    assertTrue(indent.stack.isEmpty())
  }
}
