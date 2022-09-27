package org.petitparser.core.parser

import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.misc.endOfInput
import org.petitparser.core.parser.misc.failure
import org.petitparser.core.parser.misc.position
import org.petitparser.core.parser.misc.success
import kotlin.test.Test

internal class MiscTest {
  @Test
  fun test_endOfInput() {
    val parser = endOfInput()
    assertSuccess(parser, "", Unit)
    assertFailure(parser, "1", "end of input expected")
  }

  @Test
  fun test_end() {
    val parser = any().end()
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "aa", "end of input expected", 1)
    assertFailure(parser, "", "input expected")
  }

  @Test
  fun test_failure() {
    val parser = failure<Int>("permanent failure")
    assertFailure(parser, "", "permanent failure")
    assertFailure(parser, "a", "permanent failure")
  }

  @Test
  fun test_position() {
    val parser = position()
    assertSuccess(parser, "", 0)
    assertSuccess(parser, "a", 0, 0)
  }

  @Test
  fun test_success() {
    val parser = success(42)
    assertSuccess(parser, "", 42)
    assertSuccess(parser, "a", 42, 0)
  }
}