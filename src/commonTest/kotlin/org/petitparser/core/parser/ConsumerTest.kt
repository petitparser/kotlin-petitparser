package org.petitparser.core.parser

import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.anyOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.consumer.letterOrDigit
import org.petitparser.core.parser.consumer.noneOf
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.consumer.whitespace
import kotlin.test.Test

internal class ConsumerTest {
  @Test
  fun test_any() {
    val parser = any()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "", "input expected")
  }

  @Test
  fun test_anyOfString() {
    val parser = anyOf("abc")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "1", "any of [abc] expected")
    assertFailure(parser, " ", "any of [abc] expected")
    assertFailure(parser, "", "any of [abc] expected")
  }

  @Test
  fun test_noneOf() {
    val parser = noneOf("ab1")
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "2", '2')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "a", "none of [ab1] expected")
    assertFailure(parser, "b", "none of [ab1] expected")
    assertFailure(parser, "1", "none of [ab1] expected")
    assertFailure(parser, "", "none of [ab1] expected")
  }

  @Test
  fun test_pattern_single() {
    val parser = pattern("abc")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "[abc] expected")
  }

  @Test
  fun test_pattern_range() {
    val parser = pattern("a-c")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "[a-c] expected")
  }

  @Test
  fun test_pattern_overlappingRange() {
    val parser = pattern("b-da-c")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertFailure(parser, "e", "[b-da-c] expected")
  }

  @Test
  fun test_pattern_adjacentRange() {
    val parser = pattern("c-ea-c")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "f", "[c-ea-c] expected")
  }

  @Test
  fun test_pattern_prefixRange() {
    val parser = pattern("a-ea-c")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "f", "[a-ea-c] expected")
  }

  @Test
  fun test_pattern_postfixRange() {
    val parser = pattern("a-ec-e")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "f", "[a-ec-e] expected")
  }

  @Test
  fun test_pattern_repeatedRange() {
    val parser = pattern("a-ea-e")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "f", "[a-ea-e] expected")
  }

  @Test
  fun test_pattern_composed() {
    val parser = pattern("ac-df-")
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "f", 'f')
    assertSuccess(parser, "-", '-')
    assertFailure(parser, "b", "[ac-df-] expected")
    assertFailure(parser, "e", "[ac-df-] expected")
    assertFailure(parser, "g", "[ac-df-] expected")
  }

  @Test
  fun test_pattern_negatedSingle() {
    val parser = pattern("^a")
    assertSuccess(parser, "b", 'b')
    assertFailure(parser, "a", "[^a] expected")
  }

  @Test
  fun test_pattern_negatedRange() {
    val parser = pattern("^a-c")
    assertSuccess(parser, "d", 'd')
    assertFailure(parser, "a", "[^a-c] expected")
    assertFailure(parser, "b", "[^a-c] expected")
    assertFailure(parser, "c", "[^a-c] expected")
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
  fun test_word() {
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
  fun test_char() {
    val parser = char('a')
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun test_char_category() {
    val parser = char(CharCategory.UPPERCASE_LETTER)
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "UPPERCASE_LETTER expected")
    assertFailure(parser, "", "UPPERCASE_LETTER expected")
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