package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.action.callCC
import org.petitparser.core.parser.action.cast
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.pick
import org.petitparser.core.parser.action.token
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.times
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ActionTest {
  @Test
  fun test_callCC_delegation() {
    val parser = digit().callCC { continuation, context -> continuation(context) }
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_callCC_diversion() {
    val parser = digit().callCC { _, context -> letter().parseOn(context) }
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "1", "letter expected")
  }

  @Test
  fun test_callCC_resume() {
    val continuations = mutableListOf<(Input) -> Output<Char>>()
    val inputs = mutableListOf<Input>()
    val parser = digit().callCC { continuation, input ->
      continuations.add(continuation)
      inputs.add(input)
      input.failure<Int>("Aborted")
    }
    // Execute the parser twice to collect the continuations
    val failure1 = parser.parse("1")
    assertFailure(failure1, "Aborted", 0)
    val failure2 = parser.parse("a")
    assertFailure(failure2, "Aborted", 0)
    // Later we can execute the captured continuations
    assertSuccess(continuations[0](inputs[0]), '1', 1)
    assertFailure(
      continuations[1](inputs[1]), "digit expected", 0
    )
    // Of course the continuations can be resumed multiple times
    assertSuccess(continuations[0](inputs[0]), '1', 1)
    assertFailure(continuations[1](inputs[1]), "digit expected", 0)
  }

  @Test
  fun test_cast() {
    val parser: Parser<Int> = digit().map(Char::digitToIntOrNull).cast()
    assertSuccess(parser, "1", 1)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_flatten() {
    val parser = digit().plus().flatten()
    assertSuccess(parser, "1", "1")
    assertSuccess(parser, "12", "12")
    assertSuccess(parser, "123", "123")
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_map() {
    val parser = digit().map(Char::digitToInt)
    assertSuccess(parser, "0", 0)
    assertSuccess(parser, "5", 5)
    assertSuccess(parser, "9", 9)
    assertFailure(parser, "a", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_pick() {
    val parser = digit().times(3).pick(1)
    assertSuccess(parser, "123", '2')
    assertSuccess(parser, "789", '8')
    assertFailure(parser, "abc", "digit expected")
    assertFailure(parser, "", "digit expected")
  }

  @Test
  fun test_token() {
    val parser = digit().plus().token();
    assertFailure(parser, "", "digit expected");
    assertFailure(parser, "a", "digit expected");
    val token = parser.parse("123").value;
    assertEquals(token.value, listOf('1', '2', '3'));
    assertEquals(token.buffer, "123");
    assertEquals(token.start, 0);
    assertEquals(token.stop, 3);
    assertEquals(token.input, "123");
    assertEquals(token.length, 3);
  }

  @Test
  fun test_trim() {
    val parser = digit().trim()
    assertSuccess(parser, "1", '1')
    assertSuccess(parser, " 2", '2')
    assertSuccess(parser, "  3", '3')
    assertSuccess(parser, "4 ", '4')
    assertSuccess(parser, "5  ", '5')
    assertSuccess(parser, " 6 ", '6')
    assertSuccess(parser, "  7  ", '7')
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, " a", "digit expected", 1)
    assertFailure(parser, "a ", "digit expected", 0)
    assertFailure(parser, " a ", "digit expected", 1)
  }
}