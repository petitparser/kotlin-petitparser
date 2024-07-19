package org.petitparser.grammars

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import kotlin.test.Test

internal class JsonTest {
  private val parser = JsonGrammar().start

  @Test
  fun test_true() {
    assertSuccess(parser, "true", true)
  }

  @Test
  fun test_true_invalid() {
    assertFailure(parser, "tr")
    assertFailure(parser, "trace")
    assertFailure(parser, "truest")
  }

  @Test
  fun test_false() {
    assertSuccess(parser, "false", false)
  }

  @Test
  fun test_false_invalid() {
    assertFailure(parser, "fa")
    assertFailure(parser, "falsely")
    assertFailure(parser, "fabulous")
  }

  @Test
  fun test_null() {
    assertSuccess(parser, "null", null)
  }

  @Test
  fun test_null_invalid() {
    assertFailure(parser, "nu")
    assertFailure(parser, "nuclear")
    assertFailure(parser, "nullified")
  }

  @Test
  fun test_integer() {
    assertSuccess(parser, "0", 0.0)
    assertSuccess(parser, "1", 1.0)
    assertSuccess(parser, "-1", -1.0)
    assertSuccess(parser, "12", 12.0)
    assertSuccess(parser, "-12", -12.0)
    assertSuccess(parser, "1e2", 100.0)
    assertSuccess(parser, "1e+2", 100.0)
  }

  @Test
  fun test_integer_invalid() {
    assertFailure(parser, "00")
    assertFailure(parser, "01")
  }

  @Test
  fun test_float() {
    assertSuccess(parser, "0.0", 0.0)
    assertSuccess(parser, "0.12", 0.12)
    assertSuccess(parser, "-0.12", -0.12)
    assertSuccess(parser, "12.34", 12.34)
    assertSuccess(parser, "-12.34", -12.34)
    assertSuccess(parser, "1.2e-1", 1.2e-1)
    assertSuccess(parser, "1.2E-1", 1.2e-1)
  }

  @Test
  fun test_float_invalid() {
    assertFailure(parser, ".1")
    assertFailure(parser, "0.1.1")
  }

  @Test
  fun test_string() {
    assertSuccess(parser, "\"\"", "")
    assertSuccess(parser, "\"foo\"", "foo")
    assertSuccess(parser, "\"foo bar\"", "foo bar")
  }

  @Test
  fun test_string_escaped() {
    assertSuccess(parser, "\"\\\\\"", "\\")
    assertSuccess(parser, "\"\\/\"", "/")
    assertSuccess(parser, "\"\\\"\"", "\"")
    assertSuccess(parser, "\"\\b\"", "\b")
    assertSuccess(parser, "\"\\f\"", "\u000C")
    assertSuccess(parser, "\"\\n\"", "\n")
    assertSuccess(parser, "\"\\r\"", "\r")
    assertSuccess(parser, "\"\\t\"", "\t")
  }

  @Test
  fun test_string_unicode() {
    assertSuccess(parser, "\"\\u0030\"", "0")
    assertSuccess(parser, "\"\\u007B\"", "{")
    assertSuccess(parser, "\"\\u007d\"", "}")
  }

  @Test
  fun test_string_invalid() {
    assertFailure(parser, "\"")
    assertFailure(parser, "\"foo")
    assertFailure(parser, "\"\\a\"")
    assertFailure(parser, "\"\\u00\"")
    assertFailure(parser, "\"\\u000X\"")
  }

  @Test
  fun test_array_empty() {
    assertSuccess(parser, "[]", listOf<Any?>())
  }

  @Test
  fun test_array_small() {
    assertSuccess(parser, "[0]", listOf<Any?>(0.0))
  }

  @Test
  fun test_array_large() {
    assertSuccess(parser, "[1, 2, 3]", listOf<Any?>(1.0, 2.0, 3.0))
    assertSuccess(parser, "[1, 2, 3,]", listOf<Any?>(1.0, 2.0, 3.0))
  }

  @Test
  fun test_array_nested() {
    assertSuccess(parser, "[[42]]", listOf<Any?>(listOf(42.0)))
  }

  @Test
  fun test_array_invalid() {
    assertFailure(parser, "[")
    assertFailure(parser, "[1")
    assertFailure(parser, "[1,")
    assertFailure(parser, "[1 2]")
    assertFailure(parser, "[]]")
  }

  @Test
  fun test_object_empty() {
    assertSuccess(parser, "{}", mapOf<String, Any?>())
  }

  @Test
  fun test_object_small() {
    assertSuccess(parser, "{\"a\": 1}", buildMap { put("a", 1.0) })
  }

  @Test
  fun test_object_large() {
    assertSuccess(parser, "{\"a\": 1, \"b\": 2, \"c\": 3}", buildMap {
      put("a", 1.0)
      put("b", 2.0)
      put("c", 3.0)
    })
    assertSuccess(parser, "{\"a\": 1, \"b\": 2, \"c\": 3,}", buildMap {
      put("a", 1.0)
      put("b", 2.0)
      put("c", 3.0)
    })
  }

  @Test
  fun test_object_nested() {
    assertSuccess(parser, "{\"a\": {\"b\": 2}}", buildMap {
      put("a", buildMap { put("b", 2.0) })
    })
  }

  @Test
  fun test_object_invalid() {
    assertFailure(parser, "{")
    assertFailure(parser, "{\"a\"")
    assertFailure(parser, "{\"a\":")
    assertFailure(parser, "{\"a\":\"b\"")
    assertFailure(parser, "{\"a\":\"b\",")
    assertFailure(parser, "{\"a\"}")
    assertFailure(parser, "{\"a\":}")
    assertFailure(parser, "{}}")
  }
}