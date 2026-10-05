package org.petitparser.core.parser

import org.petitparser.core.context.failure
import org.petitparser.core.context.success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ParserTest {
  @Test
  fun test_sam_conversion_and_parseOn() {
    val successParser = Parser { it.success(123) }
    assertSuccess(successParser, "abc", 123, position = 0)

    val failureParser = Parser<Int> { it.failure("error") }
    assertFailure(failureParser, "abc", message = "error", position = 0)
  }

  @Test
  fun test_default_fastParseOn() {
    val parser = Parser { it.success("ok", position = 2) }
    assertEquals(2, parser.fastParseOn("hello", 0))

    val failParser = Parser<String> { it.failure("err") }
    assertEquals(-1, failParser.fastParseOn("hello", 0))
  }

  @Test
  fun test_default_ast_methods() {
    val parser = Parser { it.success("val") }
    assertSame(parser, parser.copy())
    assertTrue(parser.children.isEmpty())

    val dummy = Parser { it.success("dummy") }
    parser.replace(dummy, parser) // should be no-op without throwing
    assertTrue(parser.children.isEmpty())
  }

  @Test
  fun test_default_isEqualTo() {
    val p1 = Parser { it.success(1) }
    val p2 = Parser { it.success(2) }

    assertTrue(p1.isEqualTo(p1))
    assertFalse(p1.isEqualTo(null))
    assertFalse(p1.isEqualTo("string"))

    // Anonymous SAM implementations may or may not have identical class, but same instance is equal
    assertTrue(p1.isEqualTo(p1))
  }

  private class CyclicParser(var other: Parser<*>? = null) : Parser<String> {
    override fun parseOn(input: org.petitparser.core.context.Input) = input.success("cycle")
    override val children: List<Parser<*>>
      get() = other?.let { listOf(it) } ?: emptyList()
  }

  @Test
  fun test_isEqualTo_with_cycle() {
    val a = CyclicParser()
    val b = CyclicParser()
    a.other = b
    b.other = a

    val x = CyclicParser()
    val y = CyclicParser()
    x.other = y
    y.other = x

    assertTrue(a.isEqualTo(x))
    assertTrue(x.isEqualTo(a))
  }

  @Test
  fun test_hasEqualChildren_different_sizes() {
    val p1 = object : Parser<Int> {
      override fun parseOn(input: org.petitparser.core.context.Input) = input.success(1)
      override val children = listOf(Parser { it.success(1) })
    }
    val p2 = object : Parser<Int> {
      override fun parseOn(input: org.petitparser.core.context.Input) = input.success(1)
      override val children = emptyList<Parser<*>>()
    }

    assertFalse(p1.isEqualTo(p2))
  }

  private class DummyParserA : Parser<String> {
    override fun parseOn(input: org.petitparser.core.context.Input) = input.success("a")
  }

  private class DummyParserB : Parser<String> {
    override fun parseOn(input: org.petitparser.core.context.Input) = input.success("b")
  }

  private class PropertyParser(val prop: Int) : Parser<Int> {
    override fun parseOn(input: org.petitparser.core.context.Input) = input.success(prop)
    override fun hasEqualProperties(other: Parser<*>) = other is PropertyParser && other.prop == prop
  }

  private class ParentParser(val child: Parser<*>) : Parser<Any?> {
    override fun parseOn(input: org.petitparser.core.context.Input) = child.parseOn(input)
    override val children = listOf(child)
  }

  @Test
  fun test_isEqualTo_different_classes() {
    val a = DummyParserA()
    val b = DummyParserB()
    assertFalse(a.isEqualTo(b))
  }

  @Test
  fun test_isEqualTo_properties() {
    val p1 = PropertyParser(1)
    val p2 = PropertyParser(2)
    val p3 = PropertyParser(1)
    assertFalse(p1.isEqualTo(p2))
    assertTrue(p1.isEqualTo(p3))
  }

  @Test
  fun test_hasEqualChildren_unequal_children() {
    val p1 = ParentParser(PropertyParser(1))
    val p2 = ParentParser(PropertyParser(2))
    assertFalse(p1.isEqualTo(p2))
  }

  @Test
  fun test_invariants() {
    val parser = Parser { it.success("leaf") }
    expectParserInvariants(parser)
  }
}
