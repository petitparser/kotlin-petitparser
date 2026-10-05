package org.petitparser.core.context

import kotlin.test.Test
import kotlin.test.assertEquals

class InputTest {
  @Test
  fun test_input_properties() {
    val input = Input.Impl("hello", 2)
    assertEquals("hello", input.buffer)
    assertEquals(2, input.position)
  }
}
