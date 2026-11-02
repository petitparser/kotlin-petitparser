package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DelegateParserTest {
  private class TestDelegateParser<T>(delegate: Parser<T>) : DelegateParser<T, T>(delegate) {
    override fun parseOn(input: Input): Output<T> = delegate.parseOn(input)
    override fun copy(): Parser<T> = TestDelegateParser(delegate)
  }

  @Test
  fun test_children() {
    val child = Parser { it.success("inner") }
    val delegateParser = TestDelegateParser(child)

    assertEquals(1, delegateParser.children.size)
    assertSame(child, delegateParser.children[0])
    assertSame(child, delegateParser.delegate)
  }

  @Test
  fun test_replace() {
    val child1 = Parser { it.success("child1") }
    val child2 = Parser { it.success("child2") }
    val dummy = Parser { it.success("dummy") }
    val delegateParser = TestDelegateParser(child1)

    // Replace non-existent child does nothing
    delegateParser.replace(dummy, child2)
    assertSame(child1, delegateParser.delegate)

    // Replace matching child updates delegate
    delegateParser.replace(child1, child2)
    assertSame(child2, delegateParser.delegate)
    assertSame(child2, delegateParser.children[0])
  }

  @Test
  fun test_invariants() {
    val child = Parser { it.success("val") }
    val delegateParser = TestDelegateParser(child)
    expectParserInvariants(delegateParser)
  }

  @Test
  fun test_equality() {
    val child1 = Parser { it.success("a") }
    val p1 = TestDelegateParser(child1)
    val p2 = TestDelegateParser(child1)

    assertTrue(p1.isEqualTo(p2))
  }
}
