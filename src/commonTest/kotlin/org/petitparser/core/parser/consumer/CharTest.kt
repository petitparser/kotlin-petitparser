package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CharTest {
  @Test
  fun test_any() {
    val parser = any()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, " ", ' ')
    assertSuccess(parser, "ab", 'a', position = 1)
    assertFailure(parser, "", "input expected")
    assertEquals("CharacterParser[input expected]", parser.toString())
  }

  @Test
  fun test_anyUnicode() {
    val parser = anyUnicode()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "\uD83E\uDD14", "\uD83E\uDD14")
    assertSuccess(parser, "\uD83E\uDD14rest", "\uD83E\uDD14", position = 2)
    assertFailure(parser, "", "input expected")
    assertEquals("UnicodeCharacterParser[input expected]", parser.toString())
  }

  @Test
  fun test_anyOf() {
    val parser = anyOf("abc")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "1", "any of [abc] expected")
    assertFailure(parser, " ", "any of [abc] expected")
    assertFailure(parser, "", "any of [abc] expected")
  }

  @Test
  fun test_anyOf_ignoreCase() {
    val parser = anyOf("aB0", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "A", 'A')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "B", 'B')
    assertSuccess(parser, "0", '0')
    assertFailure(parser, "c", "any of [aB0] expected")
    assertFailure(parser, "1", "any of [aB0] expected")
    assertFailure(parser, "", "any of [aB0] expected")
  }

  @Test
  fun test_anyOfUnicode() {
    val parser = anyOfUnicode("abc\uD83E\uDD14")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "\uD83E\uDD14", "\uD83E\uDD14")
    assertFailure(parser, "z", "any of [abc\uD83E\uDD14] expected")
    assertFailure(parser, "", "any of [abc\uD83E\uDD14] expected")
  }

  @Test
  fun test_noneOf() {
    val parser = noneOf("ab1")
    expectParserInvariants(parser)
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "2", '2')
    assertSuccess(parser, " ", ' ')
    assertFailure(parser, "a", "none of [ab1] expected")
    assertFailure(parser, "b", "none of [ab1] expected")
    assertFailure(parser, "1", "none of [ab1] expected")
    assertFailure(parser, "", "none of [ab1] expected")
  }

  @Test
  fun test_noneOf_ignoreCase() {
    val parser = noneOf("aB0", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "C", 'C')
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "none of [aB0] expected")
    assertFailure(parser, "A", "none of [aB0] expected")
    assertFailure(parser, "b", "none of [aB0] expected")
    assertFailure(parser, "B", "none of [aB0] expected")
    assertFailure(parser, "0", "none of [aB0] expected")
    assertFailure(parser, "", "none of [aB0] expected")
  }

  @Test
  fun test_noneOfUnicode() {
    val parser = noneOfUnicode("abc\uD83E\uDD14")
    expectParserInvariants(parser)
    assertSuccess(parser, "z", "z")
    assertSuccess(parser, "\uD83E\uDD10", "\uD83E\uDD10")
    assertFailure(parser, "a", "none of [abc\uD83E\uDD14] expected")
    assertFailure(parser, "\uD83E\uDD14", "none of [abc\uD83E\uDD14] expected")
    assertFailure(parser, "", "none of [abc\uD83E\uDD14] expected")
  }

  @Test
  fun test_category() {
    val parser = category(CharCategory.LOWERCASE_LETTER)
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "1", "LOWERCASE_LETTER expected")
    assertFailure(parser, "A", "LOWERCASE_LETTER expected")
    assertFailure(parser, " ", "LOWERCASE_LETTER expected")
    assertFailure(parser, "", "LOWERCASE_LETTER expected")
  }

  @Test
  fun test_digit() {
    val parser = digit()
    expectParserInvariants(parser)
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, "2", '2')
    assertSuccess(parser, "3", '3')
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")

    val custom = digit("custom digit")
    assertFailure(custom, "a", "custom digit")
  }

  @Test
  fun test_letter() {
    val parser = letter()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "Z", 'Z')
    assertFailure(parser, "1", "letter expected")
    assertFailure(parser, "", "letter expected")

    val custom = letter("custom letter")
    assertFailure(custom, "1", "custom letter")
  }

  @Test
  fun test_letterOrDigit() {
    val parser = letterOrDigit()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "*", "letter or digit expected")
    assertFailure(parser, "", "letter or digit expected")
  }

  @Test
  fun test_word() {
    val parser = word()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "Z", 'Z')
    assertSuccess(parser, "0", '0')
    assertSuccess(parser, "_", '_')
    assertFailure(parser, "-", "letter or digit expected")
    assertFailure(parser, " ", "letter or digit expected")
    assertFailure(parser, "", "letter or digit expected")

    val custom = word("word character expected")
    assertFailure(custom, "-", "word character expected")
  }

  @Test
  fun test_lowercase() {
    val parser = lowercase()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "l", 'l')
    assertSuccess(parser, "z", 'z')
    assertFailure(parser, "A", "lowercase letter expected")
    assertFailure(parser, "0", "lowercase letter expected")
    assertFailure(parser, "", "lowercase letter expected")

    val custom = lowercase("only lower")
    assertFailure(custom, "A", "only lower")
  }

  @Test
  fun test_uppercase() {
    val parser = uppercase()
    expectParserInvariants(parser)
    assertSuccess(parser, "A", 'A')
    assertSuccess(parser, "L", 'L')
    assertSuccess(parser, "Z", 'Z')
    assertFailure(parser, "a", "uppercase letter expected")
    assertFailure(parser, "0", "uppercase letter expected")
    assertFailure(parser, "", "uppercase letter expected")

    val custom = uppercase("only upper")
    assertFailure(custom, "a", "only upper")
  }

  @Test
  fun test_whitespace() {
    val parser = whitespace()
    expectParserInvariants(parser)
    assertSuccess(parser, " ", ' ')
    assertSuccess(parser, "\n", '\n')
    assertSuccess(parser, "\t", '\t')
    assertSuccess(parser, "\r", '\r')
    assertFailure(parser, "1", "whitespace expected")
    assertFailure(parser, "a", "whitespace expected")
    assertFailure(parser, "", "whitespace expected")

    val custom = whitespace("blank expected")
    assertFailure(custom, "1", "blank expected")
  }

  @Test
  fun test_char() {
    val parser = char('a')
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun test_char_ignoreCase() {
    val parser = char('a', ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun test_char_toParser() {
    val parser = 'a'.toParser()
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "", "'a' expected")
  }

  @Test
  fun test_char_toParser_message() {
    val parser = 'a'.toParser("want 'a'")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "A", "want 'a'")
    assertFailure(parser, "b", "want 'a'")
    assertFailure(parser, "", "want 'a'")
  }

  @Test
  fun test_char_toParser_caseInsensitive() {
    val parser = 'a'.toParser(ignoreCase = true, message = "want 'a' or 'A'")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "want 'a' or 'A'")
    assertFailure(parser, "", "want 'a' or 'A'")
  }

  @Test
  fun test_char_category() {
    val parser = char(CharCategory.UPPERCASE_LETTER)
    expectParserInvariants(parser)
    assertSuccess(parser, "A", 'A')
    assertFailure(parser, "b", "UPPERCASE_LETTER expected")
    assertFailure(parser, "", "UPPERCASE_LETTER expected")
  }

  @Test
  fun test_char_predicate() {
    val parser = char({ "aeiou".contains(it) }, "vowel expected")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "e", 'e')
    assertFailure(parser, "x", "vowel expected")
    assertFailure(parser, "", "vowel expected")
  }

  @Test
  fun test_range() {
    val parser = range('a', 'c')
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "[a-c] expected")
    assertFailure(parser, "", "[a-c] expected")

    val rangeFromCharRange = range('a'..'c')
    assertSuccess(rangeFromCharRange, "b", 'b')
  }

  @Test
  fun test_unicodeChar_code() {
    val parser = unicodeChar(128580, "'🙄' expected")
    expectParserInvariants(parser)
    assertSuccess(parser, "\uD83D\uDE44", "\uD83D\uDE44")
    assertFailure(parser, "\uD83E\uDD14", "'🙄' expected")
    assertFailure(parser, "a", "'🙄' expected")
    assertFailure(parser, "", "'🙄' expected")
  }

  @Test
  fun test_unicodeChar_string() {
    val parser = unicodeChar("\uD83D\uDE44")
    expectParserInvariants(parser)
    assertSuccess(parser, "\uD83D\uDE44", "\uD83D\uDE44")
    assertFailure(parser, "a", "'\uD83D\uDE44' expected")
  }

  @Test
  fun test_pattern_single() {
    val parser = pattern("abc")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "[abc] expected")
  }

  @Test
  fun test_pattern_range() {
    val parser = pattern("a-c")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertFailure(parser, "d", "[a-c] expected")
  }

  @Test
  fun test_pattern_overlappingRange() {
    val parser = pattern("b-da-c")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "d", 'd')
    assertFailure(parser, "e", "[b-da-c] expected")
  }

  @Test
  fun test_pattern_adjacentRange() {
    val parser = pattern("c-ea-c")
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
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
    expectParserInvariants(parser)
    assertSuccess(parser, "b", 'b')
    assertFailure(parser, "a", "[^a] expected")
  }

  @Test
  fun test_pattern_negatedRange() {
    val parser = pattern("^a-c")
    expectParserInvariants(parser)
    assertSuccess(parser, "d", 'd')
    assertFailure(parser, "a", "[^a-c] expected")
    assertFailure(parser, "b", "[^a-c] expected")
    assertFailure(parser, "c", "[^a-c] expected")
  }

  @Test
  fun test_pattern_ignoreCase() {
    val parser = pattern("b-d", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "b", 'b')
    assertSuccess(parser, "B", 'B')
    assertSuccess(parser, "c", 'c')
    assertSuccess(parser, "C", 'C')
    assertSuccess(parser, "d", 'd')
    assertSuccess(parser, "D", 'D')
    assertFailure(parser, "a", "[b-d] expected")
    assertFailure(parser, "A", "[b-d] expected")
    assertFailure(parser, "e", "[b-d] expected")
  }

  @Test
  fun test_patternUnicode() {
    val parser = patternUnicode("a-c\uD83E\uDD14")
    expectParserInvariants(parser)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "b", "b")
    assertSuccess(parser, "c", "c")
    assertSuccess(parser, "\uD83E\uDD14", "\uD83E\uDD14")
    assertFailure(parser, "d", "[a-c\uD83E\uDD14] expected")
    assertFailure(parser, "", "[a-c\uD83E\uDD14] expected")
  }

  @Test
  fun test_character_parser_equality_and_copy() {
    val p1 = char('a')
    val p2 = char('a')
    val p3 = char('b')

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))

    val copy = p1.copy()
    assertTrue(p1.isEqualTo(copy))
    assertEquals(p1.message, copy.message)
    assertEquals(p1.predicate, copy.predicate)
  }

  @Test
  fun test_unicode_character_parser_equality_and_copy() {
    val p1 = anyUnicode()
    val p2 = anyUnicode()
    val p3 = unicodeChar("\uD83D\uDE44")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))

    val copy = p1.copy()
    assertTrue(p1.isEqualTo(copy))
    assertEquals(p1.message, copy.message)
    assertEquals(p1.predicate, copy.predicate)
  }

  @Test
  fun test_unicode_isolated_surrogates() {
    val parser = anyUnicode()
    // Isolated high surrogate at end of input
    assertSuccess(parser, "\uD83D", "\uD83D")
    // Isolated high surrogate followed by a non-low surrogate
    assertSuccess(parser, "\uD83Da", "\uD83D", position = 1)
  }

  @Test
  fun test_category_invariants() {
    val parser1 = category(CharCategory.UPPERCASE_LETTER)
    val parser2 = char(CharCategory.UPPERCASE_LETTER)
    expectParserInvariants(parser1)
    expectParserInvariants(parser2)
    assertTrue(parser1.isEqualTo(parser2))
    assertFalse(parser1.isEqualTo(category(CharCategory.LOWERCASE_LETTER)))
  }

  @Test
  fun test_letterOrDigit_equality() {
    val p1 = letterOrDigit()
    val p2 = letterOrDigit()
    val p3 = letterOrDigit("other message")

    expectParserInvariants(p1)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
  }

  @Test
  fun test_anyOf_noneOf_iterable() {
    val anyParser = anyOf(listOf('a', 'b', 'c'))
    expectParserInvariants(anyParser)
    assertSuccess(anyParser, "a", 'a')
    assertSuccess(anyParser, "b", 'b')
    assertFailure(anyParser, "d", "any of [abc] expected")

    val noneParser = noneOf(listOf('a', 'b', 'c'))
    expectParserInvariants(noneParser)
    assertSuccess(noneParser, "d", 'd')
    assertFailure(noneParser, "a", "none of [abc] expected")
  }

  @Test
  fun test_anyOfUnicode_ignoreCase() {
    val parser = anyOfUnicode("aB\uD83E\uDD14", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "A", "A")
    assertSuccess(parser, "b", "b")
    assertSuccess(parser, "B", "B")
    assertSuccess(parser, "\uD83E\uDD14", "\uD83E\uDD14")
    assertFailure(parser, "c", "any of [aB\uD83E\uDD14] expected")
  }

  @Test
  fun test_noneOfUnicode_ignoreCase() {
    val parser = noneOfUnicode("aB\uD83E\uDD14", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "c", "c")
    assertSuccess(parser, "C", "C")
    assertFailure(parser, "a", "none of [aB\uD83E\uDD14] expected")
    assertFailure(parser, "A", "none of [aB\uD83E\uDD14] expected")
    assertFailure(parser, "\uD83E\uDD14", "none of [aB\uD83E\uDD14] expected")
  }

  @Test
  fun test_patternUnicode_ignoreCase() {
    val parser = patternUnicode("a-c\uD83E\uDD14", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "A", "A")
    assertSuccess(parser, "b", "b")
    assertSuccess(parser, "B", "B")
    assertSuccess(parser, "c", "c")
    assertSuccess(parser, "C", "C")
    assertSuccess(parser, "\uD83E\uDD14", "\uD83E\uDD14")
    assertFailure(parser, "d", "[a-c\uD83E\uDD14] expected")
    assertFailure(parser, "D", "[a-c\uD83E\uDD14] expected")
  }

  @Test
  fun test_patternUnicode_range() {
    val parser = patternUnicode("\uD83D\uDE00-\uD83D\uDE02")
    expectParserInvariants(parser)
    assertSuccess(parser, "\uD83D\uDE00", "\uD83D\uDE00")
    assertSuccess(parser, "\uD83D\uDE01", "\uD83D\uDE01")
    assertSuccess(parser, "\uD83D\uDE02", "\uD83D\uDE02")
    assertFailure(parser, "\uD83D\uDE03", "[\uD83D\uDE00-\uD83D\uDE02] expected")
  }

  @Test
  fun test_unicodeChar_invalid_string() {
    assertFailsWith<IllegalArgumentException> {
      unicodeChar("")
    }
    assertFailsWith<IllegalArgumentException> {
      unicodeChar("ab")
    }
  }

  @Test
  fun test_range_invalid() {
    assertFailsWith<IllegalArgumentException> {
      range('z', 'a')
    }
  }

  @Test
  fun test_character_parser_equality_branches() {
    val p1 = char('a')
    val pDiffMessage = char('a', message = "other message")
    val pUnicode = anyUnicode()

    assertFalse(p1.isEqualTo(pDiffMessage))
    assertFalse(p1.isEqualTo(pUnicode))
    assertEquals(-1, p1.fastParseOn("a", -1))
  }

  @Test
  fun test_unicode_character_parser_equality_branches() {
    val u1 = anyUnicode()
    val uDiffMessage = anyUnicode("other message")
    val pChar = char('a')

    assertFalse(u1.isEqualTo(uDiffMessage))
    assertFalse(u1.isEqualTo(pChar))
    assertEquals(-1, u1.fastParseOn("\uD83D\uDE00", -1))
  }
}

