package org.petitparser.core.definition

import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReferenceTest {
  @Test
  fun test_reference() {
    fun simple() = char('z')
    val r1 = ref(::simple)
    val r2 = ref(::simple)
    assertEquals(r1, r2)
    assertEquals(r1.hashCode(), r2.hashCode())
    assertTrue(r1.toString().contains("ReferenceParser"))
  }
}
