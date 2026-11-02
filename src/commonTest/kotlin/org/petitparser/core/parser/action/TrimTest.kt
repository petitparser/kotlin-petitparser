package org.petitparser.core.parser.action

import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.whitespace
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class TrimTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(digit().trim())
    expectParserInvariants(digit().trim(char('a'), char('b')))
  }

  @Test
  fun test_default() {
    val parser = char('a').trim()
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, " a", 'a')
    assertSuccess(parser, "a ", 'a')
    assertSuccess(parser, " a ", 'a')
    assertSuccess(parser, "  a", 'a')
    assertSuccess(parser, "a  ", 'a')
    assertSuccess(parser, "  a  ", 'a')
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, " b", "'a' expected", 1)
    assertFailure(parser, "  b", "'a' expected", 2)
  }

  @Test
  fun test_custom_both() {
    val parser = char('a').trim(char('*'))
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "*a", 'a')
    assertSuccess(parser, "a*", 'a')
    assertSuccess(parser, "*a*", 'a')
    assertSuccess(parser, "**a", 'a')
    assertSuccess(parser, "a**", 'a')
    assertSuccess(parser, "**a**", 'a')
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "*b", "'a' expected", 1)
    assertFailure(parser, "**b", "'a' expected", 2)
  }

  @Test
  fun test_custom_left_and_right() {
    val parser = char('a').trim(char('*'), char('#'))
    assertSuccess(parser, "a", 'a')
    assertSuccess(parser, "*a", 'a')
    assertSuccess(parser, "a#", 'a')
    assertSuccess(parser, "*a#", 'a')
    assertSuccess(parser, "**a", 'a')
    assertSuccess(parser, "a##", 'a')
    assertSuccess(parser, "**a##", 'a')
    assertFailure(parser, "", "'a' expected")
    assertFailure(parser, "b", "'a' expected")
    assertFailure(parser, "*b", "'a' expected", 1)
    assertFailure(parser, "**b", "'a' expected", 2)
    assertFailure(parser, "#a", "'a' expected", 0)
    assertSuccess(parser, "a*", 'a', 1)
  }

  @Test
  fun test_equality() {
    val p1 = digit().trim()
    val p2 = digit().trim()
    val p3 = digit().plus().trim()
    val p4 = digit().trim(left = digit().plus())
    val p5 = digit().trim(right = digit().plus())
    assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
    kotlin.test.assertFalse(p1.isEqualTo(p4))
    kotlin.test.assertFalse(p1.isEqualTo(p5))
  }

  @Test
  fun test_children_and_replace() {
    val child = digit()
    val left = whitespace()
    val right = char('#')
    val parser = child.trim(left, right) as TrimmingParser<Char>

    assertEquals(3, parser.children.size)
    assertSame(child, parser.children[0])
    assertSame(left, parser.children[1])
    assertSame(right, parser.children[2])

    val newLeft = char('*')
    parser.replace(left, newLeft)
    assertSame(newLeft, parser.left)

    val newRight = char('$')
    parser.replace(right, newRight)
    assertSame(newRight, parser.right)
  }

  @Test
  fun test_non_consuming_trimmer() {
    val nonConsuming = org.petitparser.core.parser.Parser { it.success("dummy") }
    val parser = digit().trim(nonConsuming)
    kotlin.test.assertFailsWith<IllegalStateException> {
      parser.parse("1")
    }
  }
}
