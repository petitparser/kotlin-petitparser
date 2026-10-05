package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.expectParserInvariants
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
}
