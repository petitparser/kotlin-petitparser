package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test

class AndTest {
  @Test
  fun test_and() {
    val parser = digit().and()
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1', 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "", "digit expected", 0)
  }

  @Test
  fun test_and_char() {
    val parser = char('a').and()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a', 0)
    assertFailure(parser, "b", "'a' expected", 0)
    assertFailure(parser, "", "'a' expected", 0)
  }
}
