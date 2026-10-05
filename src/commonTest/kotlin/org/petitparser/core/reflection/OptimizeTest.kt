package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.token
import org.petitparser.core.parser.combinator.ChoiceParser
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq2
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.CharacterParser
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.toParser
import org.petitparser.core.parser.misc.labeled
import org.petitparser.core.parser.repeater.RepeatingCharacterParser
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.repeat
import org.petitparser.core.parser.utils.selectFarthest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

class OptimizeTest {
  private class PluggableOptimizeRule(
    val action: (OptimizeRule, Analyzer, Parser<*>, ReplaceParser<Any?>) -> Unit,
  ) : OptimizeRule() {
    @Suppress("UNCHECKED_CAST")
    override fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>) {
      action(this, analyzer, parser, replace as ReplaceParser<Any?>)
    }
  }

  @Test
  fun test_rulesCalledOnAllParsers() {
    val seen = mutableSetOf<Parser<*>>()
    val input = char('a') or char('b')
    val rule = PluggableOptimizeRule { _, _, parser, _ -> seen.add(parser) }
    val result = optimize(input, callback = { _, _ -> fail("No callback expected") }, rules = listOf(rule))
    assertSame(input, result)
    assertEquals(setOf(input, input.children[0], input.children[1]), seen)
  }

  @Test
  fun test_rootReplacementPerformed() {
    val input = "input".toParser()
    val output = "output".toParser()
    val rule = PluggableOptimizeRule { _, _, parser, replace ->
      assertSame(input, parser)
      replace(input, output)
    }
    var callbackCalled = false
    val result = optimize(
      input,
      callback = { source, target ->
        assertSame(input, source)
        assertSame(output, target)
        callbackCalled = true
      },
      rules = listOf(rule),
    )
    assertSame(output, result)
    assertTrue(callbackCalled)
  }

  @Test
  fun test_childReplacementPerformed() {
    val input = char('a') or char('b')
    val replacement = char('c')
    val rule = PluggableOptimizeRule { _, _, parser, replace ->
      if (parser is CharacterParser && parser.message == "'b' expected") {
        replace(parser, replacement)
      }
    }
    var callbackCalled = false
    val result = optimize(
      input,
      callback = { source, target ->
        assertSame(input.children[1], source)
        assertSame(replacement, target)
        callbackCalled = true
      },
      rules = listOf(rule),
    )
    assertSame(input, result)
    assertSame(replacement, result.children[1])
    assertTrue(callbackCalled)
  }

  @Test
  fun test_characterRepeater_withPredicateParser() {
    val character = char('a')
    val parser = character.repeat(2, 3).flatten()
    val result = optimize(parser, rules = listOf(CharacterRepeater()))
    assertTrue(result is RepeatingCharacterParser)
    assertEquals(2, result.min)
    assertEquals(3, result.max)
    assertEquals("'a' expected", result.message)
  }

  @Test
  fun test_characterRepeater_withAnyParser() {
    val character = any()
    val parser = character.repeat(3, 5).flatten()
    val result = optimize(parser, rules = listOf(CharacterRepeater()))
    assertTrue(result is RepeatingCharacterParser)
    assertEquals(3, result.min)
    assertEquals(5, result.max)
    assertEquals("input expected", result.message)
  }

  @Test
  fun test_characterRepeater_withoutOptimization() {
    val parser = char('a').plus().token()
    val result = optimize(parser, rules = listOf(CharacterRepeater()))
    assertSame(parser, result)
  }

  @Test
  fun test_nestedChoice_withIssue() {
    val inner = listOf(char('2'), char('3')).toChoiceParser(failureJoiner = ::selectFarthest)
    val parser = listOf(char('1'), inner, char('4')).toChoiceParser(failureJoiner = ::selectFarthest)
    val result = optimize(parser, rules = listOf(FlattenChoice()))
    assertTrue(result is ChoiceParser<*>)
    assertEquals(
      listOf(parser.children[0], inner.children[0], inner.children[1], parser.children[2]),
      result.children,
    )
    assertSame(parser.failureJoiner, result.failureJoiner)
  }

  @Test
  fun test_nestedChoice_withoutOptimizationNoNesting() {
    val parser = listOf(char('1'), char('2'), char('3')).toChoiceParser()
    val result = optimize(
      parser,
      rules = listOf(FlattenChoice()),
      callback = { _, _ -> fail("No replacement expected") },
    )
    assertSame(parser, result)
  }

  @Test
  fun test_nestedChoice_withoutOptimizationDifferentJoiner() {
    val inner = listOf(char('2'), char('3')).toChoiceParser(failureJoiner = ::selectFarthest)
    val parser = listOf(char('1'), inner, char('4')).toChoiceParser()
    val result = optimize(
      parser,
      rules = listOf(FlattenChoice()),
      callback = { _, _ -> fail("No replacement expected") },
    )
    assertSame(parser, result)
  }

  @Test
  fun test_removeDelegate_singleSettable() {
    val parser = char('a').settable()
    val result = optimize(parser, rules = listOf(RemoveDelegate()))
    assertSame(parser.children[0], result)
  }

  @Test
  fun test_removeDelegate_singleLabel() {
    val parser = char('a').labeled("hello")
    val result = optimize(parser, rules = listOf(RemoveDelegate()))
    assertSame(parser.children[0], result)
  }

  @Test
  fun test_removeDelegate_repeatedSettable() {
    val parser = char('a').settable().settable()
    val result = optimize(parser, rules = listOf(RemoveDelegate()))
    assertSame(parser.children[0], result)
  }

  @Test
  fun test_removeDelegate_loop() {
    val parser = undefined<Any?>()
    parser.set(parser)
    val result = optimize(parser, rules = listOf(RemoveDelegate()))
    assertSame(parser, result)
  }

  @Test
  fun test_removeDuplicate_withDuplicate() {
    val parser = seq2(digit(), digit())
    val result = optimize(parser, rules = listOf(RemoveDuplicate()))
    assertSame(result.children.first(), result.children.last())
  }

  @Test
  fun test_removeDuplicate_withoutDuplicate() {
    val parser = seq2(digit(message = "first"), digit(message = "second"))
    val result = optimize(
      parser,
      rules = listOf(RemoveDuplicate()),
      callback = { _, _ -> fail("No replacement expected") },
    )
    assertNotSame(result.children.first(), result.children.last())
  }
}
