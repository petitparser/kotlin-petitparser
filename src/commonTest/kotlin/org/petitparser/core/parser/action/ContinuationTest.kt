package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ContinuationTest {
  @Test
  fun test_invariants() {
    val handler: ContinuationHandler<Char, Char> = { continuation, context -> continuation(context) }
    expectParserInvariants(any().callCC(handler))
  }

  @Test
  fun test_delegation() {
    val parser = digit().callCC { continuation, context -> continuation(context) }
    assertSuccess(parser, "1", '1')
    assertFailure(parser, "a", "digit expected")
  }

  @Test
  fun test_diversion() {
    val parser = digit().callCC { _, context -> letter().parseOn(context) }
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "1", "letter expected")
  }

  @Test
  fun test_resume() {
    val continuations = mutableListOf<(Input) -> Output<Char>>()
    val inputs = mutableListOf<Input>()
    val parser = digit().callCC { continuation, input ->
      continuations.add(continuation)
      inputs.add(input)
      input.failure("Aborted")
    }
    // Execute the parser twice to collect the continuations
    val failure1 = parser.parse("1")
    assertFailure(failure1, "Aborted", 0)
    val failure2 = parser.parse("a")
    assertFailure(failure2, "Aborted", 0)
    // Later we can execute the captured continuations
    assertSuccess(continuations[0](inputs[0]), '1', 1)
    assertFailure(continuations[1](inputs[1]), "digit expected", 0)
    // Of course the continuations can be resumed multiple times
    assertSuccess(continuations[0](inputs[0]), '1', 1)
    assertFailure(continuations[1](inputs[1]), "digit expected", 0)
  }

  @Test
  fun test_success() {
    val parser = digit().callCC { _, context -> context.success("success") }
    assertSuccess(parser, "1", "success", 0)
    assertSuccess(parser, "a", "success", 0)
  }

  @Test
  fun test_failure() {
    val parser = digit().callCC { _, context -> context.failure("failure") }
    assertFailure(parser, "1", "failure", 0)
    assertFailure(parser, "a", "failure", 0)
  }

  @Test
  fun test_equality() {
    val handler1: ContinuationHandler<Char, Char> = { continuation, context -> continuation(context) }
    val handler2: ContinuationHandler<Char, Char> = { _, context -> context.failure("err") }
    val p1 = digit().callCC(handler1)
    val p2 = digit().callCC(handler1)
    val p3 = digit().callCC(handler2)
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(digit()))
  }
}
