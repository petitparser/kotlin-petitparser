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
    assertFailsWith<UnsupportedOperationException> {
      success.message
    }
  }

  @Test
  fun test_failure() {
    val input = Input.Impl("abc", 1)
    val failure = input.failure("expected something", 1)
    assertEquals("abc", failure.buffer)
    assertEquals(1, failure.position)
    assertEquals("expected something", failure.message)
    val error = assertFailsWith<ParseError> {
      failure.value
    }
    assertEquals(failure, error.failure)
  }
}
