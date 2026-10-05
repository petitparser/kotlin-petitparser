package org.petitparser.core.context

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OutputTest {
  @Test
  fun test_success() {
    val input = Input.Impl("abc", 1)
    val success = input.success("val", 2)
    assertEquals("abc", success.buffer)
    assertEquals(2, success.position)
    assertEquals("val", success.value)
    assertEquals(1, success.line)
    assertEquals(3, success.column)
    assertEquals("1:3", success.toPositionString())
    assertFailsWith<UnsupportedOperationException> {
      success.message
    }
  }

  @Test
  fun test_failure() {
    val input = Input.Impl("abc\ndef", 5)
    val failure = input.failure("expected something", 5)
    assertEquals("abc\ndef", failure.buffer)
    assertEquals(5, failure.position)
    assertEquals("expected something", failure.message)
    assertEquals(2, failure.line)
    assertEquals(2, failure.column)
    assertEquals("2:2", failure.toPositionString())
    val error = assertFailsWith<ParseError> {
      failure.value
    }
    assertEquals(failure, error.failure)
  }
}
