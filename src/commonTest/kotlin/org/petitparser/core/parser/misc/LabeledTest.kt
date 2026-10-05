package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class LabeledTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(char('*').label("asterisk"))
    expectParserInvariants(char('*').labeled("asterisk"))
  }

  @Test
  fun test_label() {
    val parser = char('*').label("asterisk")
    assertEquals("asterisk", parser.label)
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "a", "'*' expected", 0)
    assertFailure(parser, "", "'*' expected", 0)
  }

  @Test
  fun test_labeled_alias() {
    val parser = char('x').labeled("letter-x")
    assertEquals("letter-x", parser.label)
    assertSuccess(parser, "x", 'x')
    assertFailure(parser, "y", "'x' expected", 0)
  }

  @Test
  fun test_equality() {
    val p1 = epsilon(1).label("star")
    val p2 = epsilon(1).label("star")
    val p3 = epsilon(1).label("asterisk")
    val p4 = epsilon(2).label("star")

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertFalse(p1.isEqualTo(p4))
    assertFalse(p1.isEqualTo(any()))
    assertFalse(p1.isEqualTo(null))
  }

  @Test
  fun test_child_replacement() {
    val parser = char('a').label("my-label")
    val replacement = char('b')
    parser.replace(parser.delegate, replacement)
    assertSame(replacement, parser.delegate)
    assertSuccess(parser, "b", 'b')
  }

  @Test
  fun test_to_string() {
    val parser = any().label("wildcard")
    assertEquals("LabeledParser[wildcard]", parser.toString())
  }
}
