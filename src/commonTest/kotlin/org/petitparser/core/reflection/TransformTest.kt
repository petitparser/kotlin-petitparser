package org.petitparser.core.reflection

import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.combinator.settable
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.lowercase
import org.petitparser.core.parser.consumer.uppercase
import org.petitparser.core.parser.misc.failure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TransformTest {
  @Test
  fun test_copy() {
    val input = lowercase().settable()
    val output = transformParser(input) { it }
    assertNotSame(input, output)
    assertTrue(input.isEqualTo(output))
    assertNotSame(input.children.single(), output.children.single())
  }

  @Test
  fun test_root() {
    val source = lowercase()
    val input = source
    val target = uppercase()
    val output = transformParser(input) { if (source.isEqualTo(it)) target else it }
    assertNotSame(input, output)
    assertFalse(input.isEqualTo(output))
    assertSame(input, source)
    assertSame(output, target)
  }

  @Test
  fun test_single() {
    val source = lowercase()
    val input = source.settable()
    val target = uppercase()
    val output = transformParser(input) { if (source.isEqualTo(it)) target else it }
    assertNotSame(input, output)
    assertFalse(input.isEqualTo(output))
    assertSame(source, input.children.single())
    assertSame(target, output.children.single())
  }

  @Test
  fun test_double() {
    val source = lowercase()
    val input = source + source
    val target = uppercase()
    val output = transformParser(input) { if (source.isEqualTo(it)) target else it }
    assertNotSame(input, output)
    assertFalse(input.isEqualTo(output))
    assertTrue(input.isEqualTo(source + source))
    assertSame(input.children.first(), input.children.last())
    assertTrue(output.isEqualTo(target + target))
    assertSame(output.children.first(), output.children.last())
  }

  @Test
  fun test_loopExisting() {
    val inner = failure<Unit>().settable()
    val outer = inner.settable().settable()
    inner.set(outer)
    val output = transformParser(outer) { it }
    assertNotSame(outer, output)
    assertTrue(outer.isEqualTo(output))
    val inputs = allParsers(outer).toSet()
    val outputs = allParsers(output).toSet()
    for (input in inputs) {
      assertFalse(outputs.contains(input))
    }
    for (out in outputs) {
      assertFalse(inputs.contains(out))
    }
  }

  @Test
  fun test_loopNew() {
    val source = lowercase()
    val input = source
    val inner = failure<String>().settable()
    val outer = inner.settable().settable()
    inner.set(outer)
    val output = transformParser(input) { if (source.isEqualTo(it)) outer else it }
    assertNotSame(input, output)
    assertFalse(input.isEqualTo(output))
    assertTrue(output.isEqualTo(outer))
  }
}
