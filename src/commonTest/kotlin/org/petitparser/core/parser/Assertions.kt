package org.petitparser.core.parser

import org.petitparser.core.context.Output
import org.petitparser.core.context.ParseError
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.asserter

fun <R> assertSuccess(
  parser: Parser<R>,
  input: String,
  value: R,
  position: Int = input.length,
) {
  assertTrue(parser.accept(input), "$parser should accept $input")
  val result = parser.parse(input)
  assertSuccess(result, value, position)
  assertFailsWith<UnsupportedOperationException> { result.message }
}

fun <R> assertSuccess(output: Output<R>, value: R, position: Int) = when (output) {
  is Output.Success -> {
    assertEquals(value, output.value, "value")
    assertEquals(position, output.position, "position")
  }
  is Output.Failure -> asserter.fail("Expected success, but got $output")
}

fun <R> assertFailure(
  parser: Parser<R>,
  input: String,
  message: String,
  position: Int = 0,
) {
  assertFalse(parser.accept(input), "$parser should not accept $input")
  val result = parser.parse(input)
  assertFailure(result, message, position)
  var error = assertFailsWith<ParseError>(message) { result.value }
  assertFailure(error.failure, message, position)
}

fun <R> assertFailure(output: Output<R>, message: String, position: Int) = when (output) {
  is Output.Success -> asserter.fail("Expected failure, but got $output")
  is Output.Failure -> {
    assertEquals(message, output.message, "message")
    assertEquals(position, output.position, "position")
  }
}