package org.petitparser.core.context

import kotlin.test.Test
import kotlin.test.assertEquals

class ParseErrorTest {
  @Test
  fun test_parse_error() {
    val failure = Output.Failure("input", 0, "failure message")
    val error = ParseError(failure)
    assertEquals(failure, error.failure)
    assertEquals("failure message", error.message)
  }
}
