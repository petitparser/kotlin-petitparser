package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FailureTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(failure<String>())
    expectParserInvariants(failure<Int>("custom failure"))
  }

  @Test
  fun test_failure_default() {
    val parser = failure<String>()
    assertEquals("unable to parse", parser.message)
    assertFailure(parser, "", "unable to parse", 0)
    assertFailure(parser, "a", "unable to parse", 0)
    assertFailure(parser, "abc", "unable to parse", 0)
  }

  @Test
  fun test_failure_custom_message() {
    val parser = failure<Int>("permanent failure")
    assertEquals("permanent failure", parser.message)
    assertFailure(parser, "", "permanent failure", 0)
    assertFailure(parser, "a", "permanent failure", 0)
  }

  @Test
  fun test_equality() {
    val p1 = failure<String>("error")
    val p2 = failure<Int>("error")
    val p3 = failure<String>("other")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(any()))
    assertFalse(p1.isEqualTo(null))
  }

  @Test
  fun test_fast_parse() {
    val parser = failure<String>()
    assertEquals(-1, parser.fastParseOn("abc", 2))
    assertEquals(-1, parser.fastParseOn("", 0))
  }

  @Test
  fun test_to_string() {
    assertEquals("FailureParser[unable to parse]", failure<Any?>().toString())
    assertEquals("FailureParser[custom]", failure<String>("custom").toString())
  }
}
