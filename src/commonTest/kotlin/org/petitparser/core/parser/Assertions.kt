package org.petitparser.core.parser

import org.petitparser.core.context.Failure
import org.petitparser.core.context.Result
import org.petitparser.core.context.Success
import kotlin.test.assertEquals
import kotlin.test.asserter

internal fun <R> assertSuccess(result: Result<R>, value: R, position: Int?) {
  if (result is Success<R>) {
    assertEquals(value, result.value)
    if (position != null) assertEquals(position, result.position)
  } else {
    asserter.fail("Expected success, but got $result")
  }
}

internal fun <R> assertSuccess(
  parser: Parser<R>,
  input: String,
  value: R,
  position: Int = input.length,
) = assertSuccess(parser.parse(input), value, position)

internal fun <R> assertFailure(result: Result<R>, message: String, position: Int?) {
  if (result is Failure<R>) {
    assertEquals(message, result.message)
    if (position != null) assertEquals(position, result.position)
  } else {
    asserter.fail("Expected failing parse result, but got $result")
  }
}

internal fun <R> assertFailure(
  parser: Parser<R>,
  input: String,
  message: String,
  position: Int = 0,
) =
  assertFailure(parser.parse(input), message, position)