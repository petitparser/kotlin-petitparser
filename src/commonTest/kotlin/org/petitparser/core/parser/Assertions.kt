package org.petitparser.core.parser

import org.petitparser.core.context.Failure
import org.petitparser.core.context.Success
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.asserter

fun <R> assertSuccess(parser: Parser<R>, input: String, expected: R, position: Int = input.length) {
  val result = parser.parse(input)
  if (result is Success<R>) {
    assertSame(input, result.buffer)
    assertEquals(position, result.position)
    assertEquals(expected, result.value)
  } else {
    asserter.fail("Expected successful parse result, but got $result")
  }
}

fun <R> assertFailure(parser: Parser<R>, input: String, message: String, position: Int = 0) {
  val result = parser.parse(input)
  if (result is Failure<R>) {
    assertSame(input, result.buffer)
    assertEquals(position, result.position)
    assertEquals(message, result.message)
  } else {
    asserter.fail("Expected failing parse result, but got $result")
  }
}