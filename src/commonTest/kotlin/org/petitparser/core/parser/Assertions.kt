package org.petitparser.core.parser

import org.petitparser.core.context.Output
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.asserter

internal fun <R> assertSuccess(output: Output<R>, value: R, position: Int) = when (output) {
  is Output.Success -> {
    assertEquals(value, output.value, "value")
    assertEquals(position, output.position, "position")
  }
  is Output.Failure -> asserter.fail("Expected success, but got $output")
}

internal fun <R> assertSuccess(
  parser: Parser<R>,
  input: String,
  value: R,
  position: Int = input.length,
) {
  assertTrue(parser.accept(input), "$parser should accept $input")
  assertSuccess(parser.parse(input), value, position)
}

internal fun <R> assertFailure(output: Output<R>, message: String, position: Int) = when (output) {
  is Output.Success -> asserter.fail("Expected failure, but got $output")
  is Output.Failure -> {
    assertEquals(message, output.message, "message")
    assertEquals(position, output.position, "position")
  }
}

internal fun <R> assertFailure(
  parser: Parser<R>,
  input: String,
  message: String,
  position: Int = 0,
) {
  assertFalse(parser.accept(input), "$parser should not accept $input")
  assertFailure(parser.parse(input), message, position)
}