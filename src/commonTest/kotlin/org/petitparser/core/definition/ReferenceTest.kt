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

  @Suppress("DEPRECATION")
  @Test
  fun test_deprecated_ref_aliases() {
    fun f0() = char('a')
    fun f1(a: Int) = char(a.toChar())
    fun f2(a: Int, b: Int) = char((a + b).toChar())
    fun f3(a: Int, b: Int, c: Int) = char((a + b + c).toChar())
    fun f4(a: Int, b: Int, c: Int, d: Int) = char((a + b + c + d).toChar())
    fun f5(a: Int, b: Int, c: Int, d: Int, e: Int) = char((a + b + c + d + e).toChar())
    fun f6(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = char((a + b + c + d + e + f).toChar())
    fun f7(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int) = char((a + b + c + d + e + f + g).toChar())
    fun f8(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int) = char((a + b + c + d + e + f + g + h).toChar())
    fun f9(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int, i: Int) = char((a + b + c + d + e + f + g + h + i).toChar())

    assertEquals(ref(::f0), ref0(::f0))
    assertEquals(ref(::f1, 65), ref1(::f1, 65))
    assertEquals(ref(::f2, 65, 0), ref2(::f2, 65, 0))
    assertEquals(ref(::f3, 65, 0, 0), ref3(::f3, 65, 0, 0))
    assertEquals(ref(::f4, 65, 0, 0, 0), ref4(::f4, 65, 0, 0, 0))
    assertEquals(ref(::f5, 65, 0, 0, 0, 0), ref5(::f5, 65, 0, 0, 0, 0))
    assertEquals(ref(::f6, 65, 0, 0, 0, 0, 0), ref6(::f6, 65, 0, 0, 0, 0, 0))
    assertEquals(ref(::f7, 65, 0, 0, 0, 0, 0, 0), ref7(::f7, 65, 0, 0, 0, 0, 0, 0))
    assertEquals(ref(::f8, 65, 0, 0, 0, 0, 0, 0, 0), ref8(::f8, 65, 0, 0, 0, 0, 0, 0, 0))
    assertEquals(ref(::f9, 65, 0, 0, 0, 0, 0, 0, 0, 0), ref9(::f9, 65, 0, 0, 0, 0, 0, 0, 0, 0))
  }
}
