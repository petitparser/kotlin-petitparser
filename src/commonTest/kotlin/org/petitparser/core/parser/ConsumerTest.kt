package org.petitparser.core.parser

import org.petitparser.core.parser.consumer.*
import kotlin.test.Test

internal class ConsumerTest {
  @Test
  fun test_char() {
    val parser = char('a')
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun test_any() {
    val parser = any()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "", "input expected")
  }

  @Test
  fun test_char_category() {
    val parser = char(CharCategory.UPPERCASE_LETTER)
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "UPPERCASE_LETTER expected")
    assertFailure(parser, "", "UPPERCASE_LETTER expected")
  }

  @Test
  fun test_digit() {
    val parser = digit()
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "2", '2')
    assertSuccess(parser, "3", '3')
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_letter() {
    val parser = letter()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "1", "letter expected")
    assertFailure(parser, "", "letter expected")
  }

  @Test
  fun test_letterOrDigit() {
    val parser = letterOrDigit()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "*", "letter or digit expected")
    assertFailure(parser, "", "letter or digit expected")
  }

  @Test
  fun test_whitespace() {
    val parser = whitespace()
    assertSuccess(parser, " ", ' ')
    assertSuccess(parser, "\n", '\n')
    assertFailure(parser, "1", "whitespace expected")
    assertFailure(parser, "", "whitespace expected")
  }

  @Test
  fun test_char_predicate() {
    val parser = char({ "aeiou".contains(it) }, "vowel expected")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "x", "vowel expected")
    assertFailure(parser, "", "vowel expected")
  }

  @Test
  fun test_string() {
    val parser = string("kotlin")
    assertSuccess(parser, "kotlin", "kotlin")
    assertFailure(parser, "KOTLIN", "'kotlin' expected")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun test_string_ignoreCase() {
    val parser = string("kotlin", ignoreCase = true)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "KOTLIN", "KOTLIN")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun test_string_predicate() {
    val parser = string(listOf("kotlin", "niltok")::contains, 6, "either way")
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "niltok", "niltok")
    assertFailure(parser, "kot", "either way")
    assertFailure(parser, "", "either way")
  }
}