package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.utils.selectFarthest
import kotlin.test.Test

class SettableTest {
  @Test
  fun test_settable() {
    val parser = digit().settable()
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected", 0)

    parser.delegate = letter()
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "1", "letter expected", 0)

    parser.set(digit())
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected", 0)
  }

  @Test
  fun test_undefined() {
    val parser = undefined<Char>()
    expectParserInvariants(parser)
    assertFailure(parser, "1", "undefined parser", 0)

    parser.set(digit())
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected", 0)
  }

  @Test
  fun test_settable_recursion() {
    val parser = undefined<Any>()
    parser.set(char('a') + parser or char('b'))
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "ab", listOf('a', 'b'))
    assertSuccess(parser, "aab", listOf('a', listOf('a', 'b')))
    assertFailure(parser, "c", "'b' expected", 0)
    assertFailure(parser, "ac", "'b' expected", 0)
  }

  @Test
  fun test_settable_recursion_selectFarthest() {
    val parser = undefined<Any>()
    parser.set(or(char('a') + parser, char('b'), failureJoiner = ::selectFarthest))
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "ab", listOf('a', 'b'))
    assertSuccess(parser, "aab", listOf('a', listOf('a', 'b')))
    assertFailure(parser, "c", "'b' expected", 0)
    assertFailure(parser, "ac", "'b' expected", 1)
  }

  @Test
  fun test_settable_nested_parens() {
    val parser = undefined<Any>()
    val atom = digit()
    val nested = parser.surroundedBy(char('('), char(')'))
    parser.set(atom or nested)
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "(1)", '1')
    assertSuccess(parser, "((1))", '1')
    assertFailure(parser, "(1", "')' expected", 2)
  }

  @Test
  fun test_resolve() {
    val delegate = digit()
    val parser = delegate.settable()
    kotlin.test.assertSame(delegate, parser.resolve())
  }
}
