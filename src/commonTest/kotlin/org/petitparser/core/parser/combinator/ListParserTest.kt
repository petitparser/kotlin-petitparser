package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ListParserTest {
  private class TestListParser<R>(children: Iterable<Parser<R>>) : ListParser<R, List<R>>(children) {
    constructor(vararg children: Parser<R>) : this(children.toList())

    override fun parseOn(input: Input): Output<List<R>> {
      val results = mutableListOf<R>()
      for (parser in parsers) {
        when (val res = parser.parseOn(input)) {
          is Output.Success -> results.add(res.value)
          is Output.Failure -> return res
        }
      }
      return input.success(results)
    }

    override fun copy(): Parser<List<R>> = TestListParser(parsers)
  }

  private class VarargListParser<R>(vararg children: Parser<R>) : ListParser<R, List<R>>(*children) {
    override fun parseOn(input: Input): Output<List<R>> = input.success(emptyList())
    override fun copy(): Parser<List<R>> = VarargListParser(*parsers.toTypedArray())
  }

  @Test
  fun test_constructors_and_children() {
    val p1 = Parser { it.success("a") }
    val p2 = Parser { it.success("b") }

    val listParser = TestListParser(p1, p2)
    assertEquals(2, listParser.children.size)
    assertSame(p1, listParser.children[0])
    assertSame(p2, listParser.children[1])
  }

  @Test
  fun test_vararg_constructor() {
    val p1 = Parser { it.success("a") }
    val p2 = Parser { it.success("b") }
    val listParser = VarargListParser(p1, p2)
    assertEquals(2, listParser.children.size)
  }

  @Test
  fun test_replace() {
    val p1 = Parser { it.success("a") }
    val p2 = Parser { it.success("b") }
    val p3 = Parser { it.success("c") }
    val dummy = Parser { it.success("dummy") }

    val listParser = TestListParser(listOf(p1, p2))

    // Replace non-existent child does nothing
    listParser.replace(dummy, p3)
    assertSame(p1, listParser.children[0])
    assertSame(p2, listParser.children[1])

    // Replace first child
    listParser.replace(p1, p3)
    assertSame(p3, listParser.children[0])
    assertSame(p2, listParser.children[1])
  }

  private class LeafParser(val value: String) : Parser<String> {
    override fun parseOn(input: Input): Output<String> = input.success(value)
    override fun hasEqualProperties(other: Parser<*>) = other is LeafParser && other.value == value
    override fun copy(): Parser<String> = LeafParser(value)
  }

  @Test
  fun test_equality() {
    val p1 = LeafParser("a")
    val p2 = LeafParser("b")
    val p3 = LeafParser("c")

    val l1 = TestListParser(p1, p2)
    val l2 = TestListParser(p1, p2)
    val l3 = TestListParser(p1, p3)

    assertTrue(l1.isEqualTo(l2))
    assertFalse(l1.isEqualTo(l3))
  }

  @Test
  fun test_invariants() {
    val p1 = LeafParser("a")
    val p2 = LeafParser("b")
    val listParser = TestListParser(p1, p2)
    expectParserInvariants(listParser)
  }
}
