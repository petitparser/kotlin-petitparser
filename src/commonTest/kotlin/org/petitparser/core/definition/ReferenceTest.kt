package org.petitparser.core.definition

import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
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

    val resolved = (r1 as ReferenceParser).resolve()
    assertTrue(char('z').isEqualTo(resolved))

    val copy = r1.copy()
    assertEquals(r1, copy)

    assertFailsWith<UnsupportedOperationException> {
      r1.parseOn(org.petitparser.core.context.Input.Impl("z", 0))
    }
    assertFailsWith<UnsupportedOperationException> {
      r1.fastParseOn("z", 0)
    }
  }

  @Test
  fun test_reference_arguments_and_equality() {
    fun f1(a: Int) = char('a')
    fun f2(a: Int, b: Int) = char('b')
    fun f3(a: Int, b: Int, c: Int) = char('c')
    fun f4(a: Int, b: Int, c: Int, d: Int) = char('d')
    fun f5(a: Int, b: Int, c: Int, d: Int, e: Int) = char('e')
    fun f6(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = char('f')
    fun f7(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int) = char('g')
    fun f8(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int) = char('h')
    fun f9(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int, i: Int) = char('i')

    val r1 = ref(::f1, 1)
    val r2 = ref(::f2, 1, 2)
    val r3 = ref(::f3, 1, 2, 3)
    val r4 = ref(::f4, 1, 2, 3, 4)
    val r5 = ref(::f5, 1, 2, 3, 4, 5)
    val r6 = ref(::f6, 1, 2, 3, 4, 5, 6)
    val r7 = ref(::f7, 1, 2, 3, 4, 5, 6, 7)
    val r8 = ref(::f8, 1, 2, 3, 4, 5, 6, 7, 8)
    val r9 = ref(::f9, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    assertTrue(r1 is ReferenceParser)
    assertTrue(r2 is ReferenceParser)
    assertTrue(r3 is ReferenceParser)
    assertTrue(r4 is ReferenceParser)
    assertTrue(r5 is ReferenceParser)
    assertTrue(r6 is ReferenceParser)
    assertTrue(r7 is ReferenceParser)
    assertTrue(r8 is ReferenceParser)
    assertTrue(r9 is ReferenceParser)

    // Equality with non-parser args
    assertEquals(r1, ref(::f1, 1))
    assertFalse(r1 == ref(::f1, 2))
    assertFalse(r1.equals("other"))
    assertFalse(r1.equals(r2))

    // Parser argument equality
    fun withParser(p: org.petitparser.core.parser.Parser<Char>) = p
    val rp1 = ref(::withParser, char('a'))
    val rp2 = ref(::withParser, char('a'))
    val rp3 = ref(::withParser, char('b'))
    assertEquals(rp1, rp2)
    assertFalse(rp1 == rp3)

    // Mixed argument types
    val customRef1 = ReferenceParser(::withParser, listOf(char('a'), 1)) { char('a') }
    val customRef2 = ReferenceParser(::withParser, listOf(char('a'), 2)) { char('a') }
    assertFalse(customRef1 == customRef2)

    val customRef3 = ReferenceParser(::withParser, listOf(char('a'), char('b'))) { char('a') }
    val customRef4 = ReferenceParser(::withParser, listOf(char('a'), 1)) { char('a') }
    assertFalse(customRef3 == customRef4)

    val nestedRef1 = ReferenceParser(::withParser, listOf(rp1)) { char('a') }
    val nestedRef2 = ReferenceParser(::withParser, listOf(rp1)) { char('a') }
    assertEquals(nestedRef1, nestedRef2)
  }
}
