package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringTest {
  @Test
  fun test_string() {
    val parser = string("kotlin")
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "kotlin_rocks", "kotlin", position = 6)
    assertFailure(parser, "KOTLIN", "'kotlin' expected")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
    assertEquals("StringParser['kotlin' expected]", parser.toString())
  }

  @Test
  fun test_string_custom_message() {
    val parser = string("kotlin", message = "Kotlin language wanted")
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertFailure(parser, "java", "Kotlin language wanted")
    assertFailure(parser, "", "Kotlin language wanted")
    assertEquals("StringParser[Kotlin language wanted]", parser.toString())
  }

  @Test
  fun test_string_ignoreCase() {
    val parser = string("kotlin", ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "KOTLIN", "KOTLIN")
    assertSuccess(parser, "Kotlin", "Kotlin")
    assertSuccess(parser, "kOtLiN", "kOtLiN")
    assertSuccess(parser, "KoTlIn_suffix", "KoTlIn", position = 6)
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun test_string_toParser() {
    val parser = "kotlin".toParser()
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertFailure(parser, "KOTLIN", "'kotlin' expected")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun test_string_toParser_message() {
    val parser = "kotlin".toParser(message = "Kotlin wanted")
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertFailure(parser, "KOTLIN", "Kotlin wanted")
    assertFailure(parser, "kot", "Kotlin wanted")
    assertFailure(parser, "", "Kotlin wanted")
  }

  @Test
  fun test_string_toParser_ignoreCase() {
    val parser = "kotlin".toParser(ignoreCase = true)
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "KOTLIN", "KOTLIN")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun test_string_predicate() {
    val parser = string(listOf("kotlin", "niltok")::contains, 6, "either way")
    expectParserInvariants(parser)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "niltok", "niltok")
    assertSuccess(parser, "kotlin_more", "kotlin", position = 6)
    assertFailure(parser, "kot", "either way")
    assertFailure(parser, "banana", "either way")
    assertFailure(parser, "", "either way")
    assertEquals("PredicateStringParser[either way]", parser.toString())
  }

  @Test
  fun test_string_empty() {
    val parser = string("")
    expectParserInvariants(parser)
    assertSuccess(parser, "", "")
    assertSuccess(parser, "abc", "", position = 0)

    val toParser = "".toParser()
    expectParserInvariants(toParser)
    assertSuccess(toParser, "", "")
    assertSuccess(toParser, "abc", "", position = 0)
  }

  @Test
  fun test_string_equality_and_copy() {
    val p1 = string("abc")
    val p2 = string("abc")
    val p3 = string("abc", ignoreCase = true)
    val p4 = string("xyz")
    val p5 = string("abc", message = "other message")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(p4))
    assertFalse(p1.isEqualTo(p5))
    assertFalse(p1.isEqualTo(newline()))

    val copy = p1.copy()
    assertTrue(p1.isEqualTo(copy))
    assertEquals(p1.literal, copy.literal)
    assertEquals(p1.message, copy.message)
    assertEquals(p1.ignoreCase, copy.ignoreCase)

    val copyCase = p3.copy()
    assertTrue(p3.isEqualTo(copyCase))
    assertEquals(p3.ignoreCase, copyCase.ignoreCase)

    assertEquals(-1, p1.fastParseOn("abc", -1))
  }

  @Test
  fun test_predicate_string_equality_and_copy() {
    val pred: (String) -> Boolean = { it == "foo" }
    val p1 = string(pred, 3, "foo wanted")
    val p2 = string(pred, 3, "foo wanted")
    val p3 = string(pred, 4, "foo wanted")
    val p4 = string(pred, 3, "different message")
    val p5 = string({ it == "bar" }, 3, "foo wanted")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(p4))
    assertFalse(p1.isEqualTo(p5))
    assertFalse(p1.isEqualTo(string("foo")))

    val copy = p1.copy()
    assertTrue(p1.isEqualTo(copy))
    assertEquals(p1.length, copy.length)
    assertEquals(p1.message, copy.message)
    assertEquals(p1.predicate, copy.predicate)

    assertEquals(-1, p1.fastParseOn("foo", -1))
  }
}
