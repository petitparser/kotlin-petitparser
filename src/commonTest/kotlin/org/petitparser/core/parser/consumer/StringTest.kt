package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import kotlin.test.Test

internal class StringTest {
  @Test
  fun testDefault() {
    val parser = string("kotlin")
    assertSuccess(parser, "kotlin", "kotlin")
    assertFailure(parser, "KOTLIN", "'kotlin' expected")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun testIgnoreCase() {
    val parser = string("kotlin", ignoreCase = true)
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "KOTLIN", "KOTLIN")
    assertFailure(parser, "kot", "'kotlin' expected")
    assertFailure(parser, "", "'kotlin' expected")
  }

  @Test
  fun testPredicate() {
    val parser = string(listOf("kotlin", "niltok")::contains, 6, "either way")
    assertSuccess(parser, "kotlin", "kotlin")
    assertSuccess(parser, "niltok", "niltok")
    assertFailure(parser, "kot", "either way")
    assertFailure(parser, "", "either way")
  }
}