package org.petitparser.core.parser

import org.petitparser.core.context.Output
import org.petitparser.core.context.ParseError
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.asserter

const val UNCHECKED_MESSAGE = "unchecked message"
const val UNCHECKED_POSITION = -1

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
  assertEquals(result.position, parser.fastParseOn(input, 0), "fastParseOn position for $parser on '$input'")
}

fun <R> assertSuccess(output: Output<R>, value: R, position: Int = UNCHECKED_POSITION) {
  when (output) {
    is Output.Success -> {
      assertEquals(value, output.value, "value")
      if (position != UNCHECKED_POSITION) assertEquals(position, output.position, "position")
    }
    is Output.Failure -> asserter.fail("Expected success, but got $output")
  }
}

fun <R> assertFailure(
  parser: Parser<R>,
  input: String,
  message: String = UNCHECKED_MESSAGE,
  position: Int = UNCHECKED_POSITION,
) {
  assertFalse(parser.accept(input), "$parser should not accept $input")
  val result = parser.parse(input)
  assertFailure(result, message, position)
  if (message != UNCHECKED_MESSAGE) {
    val error = assertFailsWith<ParseError>(message) { result.value }
    assertFailure(error.failure, message, position)
  }
  assertEquals(-1, parser.fastParseOn(input, 0), "fastParseOn failure for $parser on '$input'")
}

fun <R> assertFailure(
  output: Output<R>,
  message: String = UNCHECKED_MESSAGE,
  position: Int = UNCHECKED_POSITION,
) {
  when (output) {
    is Output.Success -> asserter.fail("Expected failure, but got $output")
    is Output.Failure -> {
      if (message != UNCHECKED_MESSAGE) assertEquals(message, output.message, "message")
      if (position != UNCHECKED_POSITION) assertEquals(position, output.position, "position")
    }
  }
}

fun <T> expectParserInvariants(parser: Parser<T>) {
  val copy = parser.copy()
  assertTrue(parser.isEqualTo(parser), "$parser should be equal to itself")
  assertTrue(copy.isEqualTo(copy), "$copy should be equal to itself")
  assertTrue(parser.isEqualTo(copy), "$parser should be equal to its copy")
  assertTrue(copy.isEqualTo(parser), "copy should be equal to $parser")
  assertFalse(parser.isEqualTo(null), "$parser should not be equal to null")
  assertFalse(parser.isEqualTo(Any()), "$parser should not be equal to arbitrary object")

  assertEquals(parser.children.size, copy.children.size)
  for (i in copy.children.indices) {
    val source = copy.children[i]
    val target = source.copy()
    copy.replace(source, target)
    assertSame(target, copy.children[i], "child $i should be replaced")
  }

  assertTrue(parser.toString().isNotEmpty(), "toString should not be empty")
}