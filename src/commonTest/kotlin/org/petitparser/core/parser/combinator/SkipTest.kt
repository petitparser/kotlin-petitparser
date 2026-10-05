package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals

class SkipTest {
  @Test
  fun test_skip() {
    val parser = digit().skip()
    expectParserInvariants(parser)
    assertEquals(3, parser.children.size)
    assertSuccess(parser, "1", '1')
    assertFailure(parser, ">2", "digit expected", 0)
  }

  @Test
  fun test_skip_before() {
    val parser = digit().skip(before = char('>'))
    expectParserInvariants(parser)
    assertFailure(parser, "1", "'>' expected", 0)
    assertFailure(parser, ">", "digit expected", 1)
    assertSuccess(parser, ">3", '3')
  }

  @Test
  fun test_skip_after() {
    val parser = digit().skip(after = char('<'))
    expectParserInvariants(parser)
    assertFailure(parser, "1", "'<' expected", 1)
    assertFailure(parser, ">2", "digit expected", 0)
    assertSuccess(parser, "3<", '3')
  }

  @Test
  fun test_skip_before_after() {
    val parser = digit().skip(before = char('>'), after = char('<'))
    expectParserInvariants(parser)
    assertFailure(parser, "1", "'>' expected", 0)
    assertFailure(parser, ">", "digit expected", 1)
    assertFailure(parser, ">3", "'<' expected", 2)
    assertSuccess(parser, ">4<", '4')
  }

  @Test
  fun test_followedBy() {
    val parser = digit().followedBy(char(';'))
    expectParserInvariants(parser)
    assertSuccess(parser, "5;", '5')
    assertFailure(parser, "5", "';' expected", 1)
    assertFailure(parser, "a;", "digit expected", 0)
  }

  @Test
  fun test_precededBy() {
    val parser = digit().precededBy(char('$'))
    expectParserInvariants(parser)
    assertSuccess(parser, "\$5", '5')
    assertFailure(parser, "5", "'$' expected", 0)
    assertFailure(parser, "\$a", "digit expected", 1)
  }

  @Test
  fun test_surroundedBy() {
    val parser = digit().surroundedBy(char('('), char(')'))
    expectParserInvariants(parser)
    assertSuccess(parser, "(7)", '7')
    assertFailure(parser, "7)", "'(' expected", 0)
    assertFailure(parser, "(7", "')' expected", 2)
    assertFailure(parser, "(a)", "digit expected", 1)
  }

  @Test
  fun test_surroundedBy_single() {
    val parser = digit().surroundedBy(char('"'))
    expectParserInvariants(parser)
    assertSuccess(parser, "\"9\"", '9')
    assertFailure(parser, "9\"", "'\"' expected", 0)
    assertFailure(parser, "\"9", "'\"' expected", 2)
  }
}
