package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import kotlin.test.Test

class CharTest {
  @Test
  fun testAny() {
    val parser = any()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "", "input expected")
  }

  @Test
  fun testChar() {
    val parser = char('a')
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun testCategory() {
    val parser = char(CharCategory.UPPERCASE_LETTER)
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "UPPERCASE_LETTER expected")
    assertFailure(parser, "", "UPPERCASE_LETTER expected")
  }

  @Test
  fun testDigit() {
    val parser = digit()
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "2", '2')
    assertSuccess(parser, "3", '3')
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun testLetter() {
    val parser = letter()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "1", "letter expected")
    assertFailure(parser, "", "letter expected")
  }

  @Test
  fun testWhitespace() {
    val parser = whitespace()
    assertSuccess(parser, " ", ' ')
    assertSuccess(parser, "\n", '\n')
    assertFailure(parser, "1", "whitespace expected")
    assertFailure(parser, "", "whitespace expected")
  }

  @Test
  fun testPredicate() {
    val parser = char({ "aeiou".contains(it) }, "vowel expected")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "x", "vowel expected")
    assertFailure(parser, "", "vowel expected")
  }
}