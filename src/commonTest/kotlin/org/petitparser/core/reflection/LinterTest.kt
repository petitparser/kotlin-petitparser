package org.petitparser.core.reflection

import org.petitparser.core.definition.ResolvableParser
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.cast
import org.petitparser.core.parser.action.castList
import org.petitparser.core.parser.action.constant
import org.petitparser.core.parser.action.filter
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.permute
import org.petitparser.core.parser.action.pick
import org.petitparser.core.parser.action.token
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.starSeparated
import org.petitparser.core.parser.repeater.starString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LinterTest {
  private class PluggableLinterRule(
    type: LinterType,
    title: String,
    val action: (LinterRule, Analyzer, Parser<*>, (LinterIssue) -> Unit) -> Unit,
  ) : LinterRule(type, title) {
    override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
      action(this, analyzer, parser, callback)
    }
  }

  @Test
  fun test_rules_called_on_all_parsers() {
    val seen = mutableSetOf<Parser<*>>()
    val input = char('a') or char('b')
    val rule = PluggableLinterRule(LinterType.ERROR, "Fake Rule") { _, _, parser, _ ->
      seen.add(parser)
    }
    val results = linter(input, rules = listOf(rule))
    assertTrue(results.isEmpty())
    assertEquals(setOf(input, input.children[0], input.children[1]), seen)
  }

  @Test
  fun test_issue_triggered() {
    val input = string("trigger")
    val called = mutableListOf<LinterIssue>()
    val rule = PluggableLinterRule(LinterType.ERROR, "Fake Rule") { r, _, parser, callback ->
      if (parser === input) {
        callback(LinterIssue(r, parser, "Described"))
      }
    }
    val results = linter(input, rules = listOf(rule), callback = { called.add(it) })
    assertEquals(1, results.size)
    assertSame(rule, results[0].rule)
    assertEquals(LinterType.ERROR, results[0].type)
    assertEquals("Fake Rule", results[0].title)
    assertSame(input, results[0].parser)
    assertEquals("Described", results[0].description)
    assertEquals(called, results)
    assertTrue(rule.toString().contains("Fake Rule"))
    assertTrue(results[0].toString().contains("Fake Rule"))
  }

  @Test
  fun test_character_repeater_rule() {
    val rules = listOf(CharacterRepeaterRule())
    val p1 = char('a').star().flatten()
    val r1 = linter(p1, rules = rules)
    assertEquals(1, r1.size)
    assertEquals("Character repeater", r1[0].title)

    val p2 = any().plus().flatten()
    val r2 = linter(p2, rules = rules)
    assertEquals(1, r2.size)

    val p3 = char('a').plus().token()
    val r3 = linter(p3, rules = rules)
    assertTrue(r3.isEmpty())

    val p4 = (char('a') + char('b')).flatten()
    val r4 = linter(p4, rules = rules)
    assertTrue(r4.isEmpty())

    val p5 = string("abc").star().flatten()
    val r5 = linter(p5, rules = rules)
    assertTrue(r5.isEmpty())
  }

  @Test
  fun test_duplicate_parser_rule() {
    val rules = listOf(DuplicateParserRule())
    val parser = seq(digit(), digit())
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Duplicate parser", results[0].title)
    assertSame(parser.children[0], results[0].parser)

    val parser2 = seq(digit("first"), digit("second"))
    val results2 = linter(parser2, rules = rules)
    assertTrue(results2.isEmpty())
  }

  @Test
  fun test_left_recursion_rule() {
    val rules = listOf(LeftRecursionRule())
    val parser = undefined<Any?>()
    parser.set(parser)
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Left recursion", results[0].title)
  }

  @Test
  fun test_nested_choice_rule() {
    val rules = listOf(NestedChoiceRule())
    val inner = listOf(char('2'), char('3')).toChoiceParser()
    val parser = listOf(char('1'), inner, char('4')).toChoiceParser()
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Nested choice", results[0].title)
  }

  @Test
  fun test_nullable_repeater_rule() {
    val rules = listOf(NullableRepeaterRule())
    val nullable = char('a').star()
    val parser = nullable.star()
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Nullable repeater", results[0].title)

    // Separated with non-nullable separator is not an issue
    val separated = nullable.starSeparated(char(','))
    val resultsSep = linter(separated, rules = rules)
    assertTrue(resultsSep.isEmpty())
  }

  @Test
  fun test_overlapping_choice_rule() {
    val rules = listOf(OverlappingChoiceRule())
    val parser = (char('a') + char('b')) or (char('a') + char('c'))
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Overlapping choice", results[0].title)
  }

  @Test
  fun test_repeated_choice_rule() {
    val rules = listOf(RepeatedChoiceRule())
    val parser = char('a') or char('a')
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Repeated choice", results[0].title)
  }

  @Test
  fun test_unnecessary_flatten_rule() {
    val rules = listOf(UnnecessaryFlattenRule())
    val delegates = listOf(
      char('a'),
      string("abc"),
      string("abc").flatten(),
      newline(),
      char('a').starString(),
    )
    for (delegate in delegates) {
      val parser = delegate.flatten()
      val results = linter(parser, rules = rules)
      val expected = if (delegate is org.petitparser.core.parser.action.FlattenParser) 2 else 1
      assertEquals(expected, results.size, "Expected issues for $delegate")
      assertTrue(results.all { it.title == "Unnecessary flatten" })
    }

    val customMessage = string("abc").flatten(message = "custom error")
    assertTrue(linter(customMessage, rules = rules).isEmpty())

    val nonStringDelegate = (char('a') + char('b')).flatten()
    assertTrue(linter(nonStringDelegate, rules = rules).isEmpty())
  }

  private class DummyResolvable(var target: Parser<String>) : ResolvableParser<String> {
    override fun resolve() = target
    override fun parseOn(input: org.petitparser.core.context.Input) = target.parseOn(input)
  }

  @Test
  fun test_unnecessary_resolvable_rule() {
    val rules = listOf(UnnecessaryResolvableRule())
    val parser = DummyResolvable(string("abc"))
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Unnecessary resolvable", results[0].title)
  }

  @Test
  fun test_unoptimized_flatten_rule() {
    val rules = listOf(UnoptimizedFlattenRule())
    val parser = digit().plus().flatten()
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Unoptimized flatten", results[0].title)

    val withMessage = digit().plus().flatten("digits expected")
    val resultsMsg = linter(withMessage, rules = rules)
    assertTrue(resultsMsg.isEmpty())
  }

  @Test
  fun test_unreachable_choice_rule() {
    val rules = listOf(UnreachableChoiceRule())
    val parser = char('a').star() or char('b')
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Unreachable choice", results[0].title)
  }

  @Test
  fun test_unresolved_settable_rule() {
    val rules = listOf(UnresolvedSettableRule())
    val parser = undefined<String>()
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Unresolved settable", results[0].title)
  }

  @Test
  fun test_unused_result_rule() {
    val rules = listOf(UnusedResultRule())
    val parser = digit().map { it.digitToInt() }.flatten()
    val results = linter(parser, rules = rules)
    assertEquals(1, results.size)
    assertEquals("Unused result", results[0].title)
  }

  @Test
  fun test_unused_result_rule_all_producers() {
    val rules = listOf(UnusedResultRule())
    val producers = listOf(
      char('a').cast<Any>(),
      char('a').plus().castList<Any>(),
      char('a').constant(42),
      char('a').flatten(),
      char('a').map { it },
      (char('a') + char('b')).permute(1, 0),
      (char('a') + char('b')).pick(0),
      char('a').token(),
      char('a').filter { true },
    )
    for (producer in producers) {
      val results = linter(producer.flatten(), rules = rules)
      assertEquals(1, results.size, "Expected 1 issue for $producer")
      assertEquals("Unused result", results[0].title)
    }

    val sideEffectMap = char('a').map(hasSideEffects = true) { it }
    val sideEffectResults = linter(sideEffectMap.flatten(), rules = rules)
    assertTrue(sideEffectResults.isEmpty())
  }

  @Test
  fun test_excluded_rules_and_types() {
    val parser = char('a') or char('a')
    val all = linter(parser, excludedTypes = emptySet())
    assertTrue(all.isNotEmpty())

    val excluded = linter(parser, excludedRules = setOf("Repeated choice"))
    assertTrue(excluded.isEmpty())
  }
}
