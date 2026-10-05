package org.petitparser.core.parser.misc

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.and
import org.petitparser.core.parser.combinator.not
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.star
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

  @Test
  fun test_fast_parse_at_offset() {
    val parser = char('*').label("star")
    assertEquals(2, parser.fastParseOn("a*b", 1))
    assertEquals(-1, parser.fastParseOn("a*b", 0))
    assertEquals(-1, parser.fastParseOn("a*b", 2))
  }

  @Test
  fun test_labeled_empty_in_repeater_throws() {
    val parser = epsilon(42).label("empty").star()
    kotlin.test.assertFailsWith<IllegalStateException> {
      parser.parse("abc")
    }
    kotlin.test.assertFailsWith<IllegalStateException> {
      parser.fastParseOn("abc", 0)
    }
  }

  @Test
  fun test_lookahead_with_labeled_parser() {
    val pAnd = epsilon("val").label("eps").and()
    assertSuccess(pAnd, "abc", "val", 0)

    val pNot = epsilon("val").label("eps").not()
    assertFailure(pNot, "abc", "no success expected", 0)

    val pEofAnd = endOfInput().label("eof").and()
    assertSuccess(pEofAnd, "", Unit, 0)
    assertFailure(pEofAnd, "a", "end of input expected", 0)
  }

  @Test
  fun test_nested_labels() {
    val parser = char('x').label("inner").label("outer")
    assertEquals("outer", parser.label)
    assertEquals("inner", (parser.delegate as LabeledParser).label)
    assertSuccess(parser, "x", 'x')
    assertEquals("LabeledParser[outer]", parser.toString())
  }

  @Test
  fun test_has_equal_properties() {
    val p1 = epsilon(1).label("test")
    val p2 = epsilon(2).label("test")
    val p3 = epsilon(1).label("other")
    assertTrue(p1.hasEqualProperties(p2))
    assertFalse(p1.hasEqualProperties(p3))
    assertFalse(p1.hasEqualProperties(any()))
  }
}
