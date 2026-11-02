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

  @Test
  fun test_input_factory() {
    val input0 = Input("hello")
    assertEquals("hello", input0.buffer)
    assertEquals(0, input0.position)

    val input2 = Input("hello", 2)
    assertEquals("hello", input2.buffer)
    assertEquals(2, input2.position)
  }

  @Test
  fun test_input_position_diagnostics() {
    val input = Input("hello\nworld\r\npetitparser", 8)
    assertEquals(2, input.line)
    assertEquals(3, input.column)
    assertEquals("2:3", input.toPositionString())

    val start = Input("hello\nworld", 0)
    assertEquals(1, start.line)
    assertEquals(1, start.column)
    assertEquals("1:1", start.toPositionString())

    val line3 = Input("hello\nworld\npetit", 14)
    assertEquals(3, line3.line)
    assertEquals(3, line3.column)
    assertEquals("3:3", line3.toPositionString())
  }
}
