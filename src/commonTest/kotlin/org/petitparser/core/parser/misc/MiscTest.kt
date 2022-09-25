package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import kotlin.test.Test

internal class MiscTest {
  @Test
  fun testEndOfInput() {
    val parser = endOfInput()
    assertSuccess(parser, "", Unit)
    assertFailure(parser, "1", "end of input expected")
  }

  @Test
  fun testEnd() {
    val parser = any().end()
    assertSuccess(parser, "a", 'a')
    assertFailure(parser, "aa", "end of input expected", 1)
    assertFailure(parser, "", "input expected")
  }

  @Test
  fun testFailure() {
    val parser = failure<Int>("permanent failure")
    assertFailure(parser, "", "permanent failure")
    assertFailure(parser, "a", "permanent failure")
  }

  @Test
  fun testPosition() {
    val parser = position()
    assertSuccess(parser, "", 0)
    assertSuccess(parser, "a", 0, 0)
  }

  @Test
  fun testSuccess() {
    val parser = success(42)
    assertSuccess(parser, "", 42)
    assertSuccess(parser, "a", 42, 0)
  }
}