package org.petitparser.core.parser

import org.petitparser.core.context.Context
import org.petitparser.core.context.Result
import org.petitparser.core.parser.action.*
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.combinator.repeat
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import kotlin.test.Test

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
    val continuations = mutableListOf<(Context) -> Result<Char>>()
    val contexts = mutableListOf<Context>()
    val parser = digit().callCC { continuation, context ->
      continuations.add(continuation)
      contexts.add(context)
      context.failure<Char>("Abort")
    }
    // Execute the parser twice to collect the continuations
    assertFailure(parser, "1", "Abort")
    assertFailure(parser, "a", "Abort")
    // Later we can execute the captured continuations
    assertSuccess(continuations[0](contexts[0]), '1', 1)
    assertFailure(continuations[1](contexts[1]), "digit expected", 0)
    // Of course the continuations can be resumed multiple times
    assertSuccess(continuations[0](contexts[0]), '1', 1)
    assertFailure(continuations[1](contexts[1]), "digit expected", 0)
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
    val parser = digit().repeat(3).pick(1)
    assertSuccess(parser, "123", '2')
    assertSuccess(parser, "789", '8')
    assertFailure(parser, "abc", "digit expected")
    assertFailure(parser, "", "digit expected")
  }
}