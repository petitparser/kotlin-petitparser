package org.petitparser.core.parser.utils

import org.petitparser.core.context.Output
import kotlin.test.Test
import kotlin.test.assertEquals

class FailureJoinerTest {
  @Test
  fun test_select_first() {
    val f1 = Output.Failure("input", 1, "first")
    val f2 = Output.Failure("input", 2, "second")
    assertEquals(f1, selectFirst(f1, f2))
  }

  @Test
  fun test_select_last() {
    val f1 = Output.Failure("input", 1, "first")
    val f2 = Output.Failure("input", 2, "second")
    assertEquals(f2, selectLast(f1, f2))
  }

  @Test
  fun test_select_farthest() {
    val f1 = Output.Failure("input", 1, "first")
    val f2 = Output.Failure("input", 2, "second")
    val f3 = Output.Failure("input", 1, "third")
    assertEquals(f2, selectFarthest(f1, f2))
    assertEquals(f2, selectFarthest(f2, f1))
    assertEquals(f3, selectFarthest(f1, f3))
  }

  @Test
  fun test_select_farthest_joined() {
    val f1 = Output.Failure("input", 1, "first")
    val f2 = Output.Failure("input", 2, "second")
    val f3 = Output.Failure("input", 1, "third")
    assertEquals(f2, selectFarthestJoined(f1, f2))
    assertEquals(f2, selectFarthestJoined(f2, f1))
    val joined = selectFarthestJoined(f1, f3)
    assertEquals(1, joined.position)
    assertEquals("first OR third", joined.message)
  }
}
