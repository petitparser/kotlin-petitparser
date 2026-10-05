package org.petitparser.core.parser.repeater

import org.petitparser.core.parser.action.FlattenParser
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CharacterTest {
  @Test
  fun test_invariants() {
    val parser = char('a').starString()
    assertIs<RepeatingCharacterParser>(parser)
    expectParserInvariants(parser)
  }

  @Test
  fun test_repeating_character_bounds() {
    val error1 = assertFailsWith<IllegalArgumentException> {
      char('a').repeatString(-1, 2)
    }
    assertEquals("min must be at least 0, but got -1", error1.message)

    val error2 = assertFailsWith<IllegalArgumentException> {
      char('a').repeatString(3, 2)
    }
    assertEquals("max must be at least 3, but got 2", error2.message)
  }

  @Test
  fun test_repeating_character_equality() {
    val charA = char('a')
    val p1 = charA.repeatString(1, 2)
    val p2 = charA.repeatString(1, 2)
    val pDiffMin = charA.repeatString(0, 2)
    val pDiffMax = charA.repeatString(1, 3)
    val pDiffChar = char('b').repeatString(1, 2)

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(pDiffMin))
    assertFalse(p1.isEqualTo(pDiffMax))
    assertFalse(p1.isEqualTo(pDiffChar))
  }

  @Test
  fun test_repeating_character_toString() {
    assertEquals("RepeatingCharacterParser['a' expected, 0..*]", char('a').starString().toString())
    assertEquals("RepeatingCharacterParser['a' expected, 1..*]", char('a').plusString().toString())
    assertEquals("RepeatingCharacterParser['a' expected, 2..2]", char('a').timesString(2).toString())
    assertEquals("RepeatingCharacterParser['a' expected, 2..4]", char('a').repeatString(2, 4).toString())
  }

  @Test
  fun test_string_star() {
    val parser = char('a').starString()
    assertSuccess(parser, "", "")
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "aa", "aa")
    assertSuccess(parser, "aaa", "aaa")
  }

  @Test
  fun test_string_plus() {
    val parser = char('a').plusString()
    assertFailure(parser, "", "'a' expected", 0)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "aa", "aa")
    assertSuccess(parser, "aaa", "aaa")
  }

  @Test
  fun test_string_times() {
    val parser = char('a').timesString(2)
    assertFailure(parser, "", "'a' expected", 0)
    assertFailure(parser, "a", "'a' expected", 1)
    assertSuccess(parser, "aa", "aa")
    assertSuccess(parser, "aaa", "aa", 2)
  }

  @Test
  fun test_string_repeat() {
    val parser = char('a').repeatString(1, 2)
    assertFailure(parser, "", "'a' expected", 0)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "aa", "aa")
    assertSuccess(parser, "aaa", "aa", 2)
  }

  @Test
  fun test_custom_message() {
    val parser = char('a').plusString("custom error")
    assertFailure(parser, "", "custom error", 0)
    assertSuccess(parser, "a", "a")
  }

  @Test
  fun test_fallback_non_char_parser() {
    val parser = char('a').settable().plusString()
    assertIs<FlattenParser>(parser)
    assertFailure(parser, "", "'a' expected", 0)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "aa", "aa")
    assertSuccess(parser, "aaa", "aaa")
  }

  @Test
  fun test_large_input() {
    val input = "a".repeat(10_000)
    val parser = char('a').plusString()
    assertSuccess(parser, input, input)
  }
}
