package org.petitparser.core.parser.repeater

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.expectParserInvariants
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SeparatedTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(digit().starSeparated(letter()))
    expectParserInvariants(digit().plusSeparated(letter()))
    expectParserInvariants(digit().timesSeparated(letter(), 3))
    expectParserInvariants(digit().repeatSeparated(letter(), 2, 4))
  }

  @Test
  fun test_children_and_replace() {
    val delegate = char('a')
    val separator = char(',')
    val parser = delegate.starSeparated(separator)

    assertEquals(listOf(delegate, separator), parser.children)

    val newDelegate = char('b')
    parser.replace(delegate, newDelegate)
    assertEquals(newDelegate, parser.delegate)

    val newSeparator = char(';')
    parser.replace(separator, newSeparator)
    assertEquals(newSeparator, parser.separator)
  }

  @Test
  fun test_separated_equality() {
    val p1 = digit().repeatSeparated(letter(), 1, 3)
    val p2 = digit().repeatSeparated(letter(), 1, 3)
    val pDiffMin = digit().repeatSeparated(letter(), 2, 3)

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(pDiffMin))
  }

  @Test
  fun test_separated_star() {
    val parser = digit().starSeparated(letter())
    assertSuccess(parser, "", SeparatedList(listOf(), listOf()))
    assertSuccess(parser, "a", SeparatedList(listOf(), listOf()), 0)
    assertSuccess(parser, "1", SeparatedList(listOf('1'), listOf()))
    assertSuccess(parser, "1a", SeparatedList(listOf('1'), listOf()), 1)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3;", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(
      parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c'))
    )
    assertSuccess(
      parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')), 7
    )
  }

  @Test
  fun test_separated_plus() {
    val parser = digit().plusSeparated(letter())
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertSuccess(parser, "1", SeparatedList(listOf('1'), listOf()))
    assertSuccess(parser, "1a", SeparatedList(listOf('1'), listOf()), 1)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(
      parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c'))
    )
    assertSuccess(
      parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3', '4'), listOf('a', 'b', 'c')), 7
    )
  }

  @Test
  fun test_separated_times() {
    val parser = digit().timesSeparated(letter(), 3)
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "1a", "digit expected", 2)
    assertFailure(parser, "1a2", "letter expected", 3)
    assertFailure(parser, "1a2b", "digit expected", 4)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
  }

  @Test
  fun test_separated_repeat() {
    val parser = digit().repeatSeparated(letter(), 2, 3)
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "a", "digit expected", 0)
    assertFailure(parser, "1", "letter expected", 1)
    assertFailure(parser, "1a", "digit expected", 2)
    assertSuccess(parser, "1a2", SeparatedList(listOf('1', '2'), listOf('a')))
    assertSuccess(parser, "1a2b", SeparatedList(listOf('1', '2'), listOf('a')), 3)
    assertSuccess(parser, "1a2b3", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')))
    assertSuccess(parser, "1a2b3c", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
    assertSuccess(parser, "1a2b3c4d", SeparatedList(listOf('1', '2', '3'), listOf('a', 'b')), 5)
  }

  @Test
  fun test_separated_list_validation() {
    val error = assertFailsWith<IllegalArgumentException> {
      SeparatedList(listOf("1"), listOf("+"))
    }
    assertTrue(error.message?.contains("Inconsistent number of elements") == true)
  }

  @Test
  fun test_separated_list_accessors_and_destructuring() {
    val list = SeparatedList(listOf(1, 2, 3), listOf("+", "-"))
    assertEquals(listOf(1, 2, 3), list.elements)
    assertEquals(listOf("+", "-"), list.separators)

    val (elements, separators) = list
    assertEquals(listOf(1, 2, 3), elements)
    assertEquals(listOf("+", "-"), separators)
  }

  @Test
  fun test_separated_list_sequential() {
    val empty = SeparatedList<String, String>(emptyList(), emptyList())
    assertEquals(emptyList(), empty.sequential.toList())

    val single = SeparatedList(listOf("1"), emptyList<String>())
    assertEquals(listOf("1"), single.sequential.toList())

    val multiple = SeparatedList(listOf(1, 2, 3), listOf("+", "-"))
    assertEquals(listOf<Any?>(1, "+", 2, "-", 3), multiple.sequential.toList())
  }

  @Test
  fun test_separated_list_foldLeft() {
    val empty = SeparatedList<String, String>(emptyList(), emptyList())
    assertFailsWith<NoSuchElementException> {
      empty.foldLeft { l, s, r -> "$l$s$r" }
    }

    val single = SeparatedList(listOf("1"), emptyList<String>())
    assertEquals("1", single.foldLeft { l, s, r -> "$l$s$r" })

    val multiple = SeparatedList(listOf(1, 2, 3), listOf("-", "-"))
    val result = multiple.foldLeft { l, _, r -> l - r }
    assertEquals((1 - 2) - 3, result) // -4
  }

  @Test
  fun test_separated_list_foldRight() {
    val empty = SeparatedList<String, String>(emptyList(), emptyList())
    assertFailsWith<NoSuchElementException> {
      empty.foldRight { l, s, r -> "$l$s$r" }
    }

    val single = SeparatedList(listOf("1"), emptyList<String>())
    assertEquals("1", single.foldRight { l, s, r -> "$l$s$r" })

    val multiple = SeparatedList(listOf(1, 2, 3), listOf("-", "-"))
    val result = multiple.foldRight { l, _, r -> l - r }
    assertEquals(1 - (2 - 3), result) // 2
  }
}
