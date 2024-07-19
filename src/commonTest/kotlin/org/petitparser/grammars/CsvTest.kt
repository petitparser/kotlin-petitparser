package org.petitparser.grammars

import org.petitparser.core.parser.assertSuccess
import kotlin.test.Test

internal class CsvTest {
  private val parser = CsvGrammar().start

  @Test
  fun test_basic_string() {
    assertSuccess(parser, "", listOf(listOf("")))
    assertSuccess(parser, "a", listOf(listOf("a")))
    assertSuccess(parser, "ab", listOf(listOf("ab")))
    assertSuccess(parser, "abc", listOf(listOf("abc")))
  }

  @Test
  fun test_quoted_string() {
    assertSuccess(parser, "\"\"", listOf(listOf("")))
    assertSuccess(parser, "\"a\"", listOf(listOf("a")))
    assertSuccess(parser, "\"ab\"", listOf(listOf("ab")))
    assertSuccess(parser, "\"abc\"", listOf(listOf("abc")))
    assertSuccess(parser, "\"\"\"\"", listOf(listOf("\"")))
  }

  @Test
  fun test_record() {
    assertSuccess(parser, "", listOf(listOf("")))
    assertSuccess(parser, "a", listOf(listOf("a")))
    assertSuccess(parser, "a,b", listOf(listOf("a", "b")))
    assertSuccess(parser, "a,b,c", listOf(listOf("a", "b", "c")))
  }

  @Test
  fun test_lines() {
    assertSuccess(parser, "", listOf(listOf("")))
    assertSuccess(parser, "a", listOf(listOf("a")))
    assertSuccess(parser, "a\nb", listOf(listOf("a"), listOf("b")))
    assertSuccess(parser, "a\nb\nc", listOf(listOf("a"), listOf("b"), listOf("c")))
  }
}