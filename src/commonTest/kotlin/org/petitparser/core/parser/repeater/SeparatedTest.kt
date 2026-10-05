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
  fun test_separated_bounds() {
    val error1 = assertFailsWith<IllegalArgumentException> {
      digit().repeatSeparated(letter(), -1, 2)
    }
    assertEquals("min must be at least 0, but got -1", error1.message)

    val error2 = assertFailsWith<IllegalArgumentException> {
      digit().repeatSeparated(letter(), 3, 2)
    }
    assertEquals("max must be at least 3, but got 2", error2.message)
  }

  @Test
  fun test_separated_default_max() {
    val parser = digit().repeatSeparated(letter(), 2)
    assertEquals(2, parser.min)
    assertEquals(2, parser.max)
  }

  @Test
  fun test_separated_times_zero() {
    val parser = digit().timesSeparated(letter(), 0)
    assertSuccess(parser, "", SeparatedList(listOf(), listOf()))
    assertSuccess(parser, "1", SeparatedList(listOf(), listOf()), 0)
    assertSuccess(parser, "a", SeparatedList(listOf(), listOf()), 0)
  }

  @Test
  fun test_separated_times_negative() {
    val error = assertFailsWith<IllegalArgumentException> {
      digit().timesSeparated(letter(), -1)
    }
    assertEquals("min must be at least 0, but got -1", error.message)
  }

  @Test
  fun test_separated_toString() {
    assertEquals("SeparatedParser[0..*]", digit().starSeparated(letter()).toString())
    assertEquals("SeparatedParser[1..*]", digit().plusSeparated(letter()).toString())
    assertEquals("SeparatedParser[2..2]", digit().timesSeparated(letter(), 2).toString())
    assertEquals("SeparatedParser[2..4]", digit().repeatSeparated(letter(), 2, 4).toString())
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
    assertEquals(listOf(newDelegate, separator), parser.children)

    val newSeparator = char(';')
    parser.replace(separator, newSeparator)
    assertEquals(newSeparator, parser.separator)
    assertEquals(listOf(newDelegate, newSeparator), parser.children)
  }

  @Test
  fun test_separated_equality() {
    val p1 = digit().repeatSeparated(letter(), 1, 3)
    val p2 = digit().repeatSeparated(letter(), 1, 3)
    val pDiffMin = digit().repeatSeparated(letter(), 2, 3)
    val pDiffMax = digit().repeatSeparated(letter(), 1, 4)
    val pDiffSep = digit().repeatSeparated(letter().star(), 1, 3)
    val pDiffDelegate = digit().star().repeatSeparated(letter(), 1, 3)

    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(pDiffMin))
    assertFalse(p1.isEqualTo(pDiffMax))
    assertFalse(p1.isEqualTo(pDiffSep))
    assertFalse(p1.isEqualTo(pDiffDelegate))
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
    val error1 = assertFailsWith<IllegalArgumentException> {
      SeparatedList(listOf("1"), listOf("+"))
    }
    assertTrue(error1.message?.contains("Inconsistent number of elements") == true)

    val error2 = assertFailsWith<IllegalArgumentException> {
      SeparatedList(listOf("1", "2"), emptyList<String>())
    }
    assertTrue(error2.message?.contains("Inconsistent number of elements") == true)

    val error3 = assertFailsWith<IllegalArgumentException> {
      SeparatedList(listOf("1", "2"), listOf("+", "-"))
    }
    assertTrue(error3.message?.contains("Inconsistent number of elements") == true)

    val error4 = assertFailsWith<IllegalArgumentException> {
      SeparatedList(emptyList<String>(), listOf("+"))
    }
    assertTrue(error4.message?.contains("Inconsistent number of elements") == true)
  }

  @Test
  fun test_separated_list_accessors_and_destructuring() {
    val list = SeparatedList(listOf(1, 2, 3), listOf("+", "-"))
    assertEquals(listOf(1, 2, 3), list.elements)
    assertEquals(listOf("+", "-"), list.separators)

    val (elements, separators) = list
    assertEquals(listOf(1, 2, 3), elements)
    assertEquals(listOf("+", "-"), separators)

    val copy = list.copy()
    assertEquals(list, copy)
    assertEquals(list.hashCode(), copy.hashCode())
    assertEquals("SeparatedList(elements=[1, 2, 3], separators=[+, -])", list.toString())
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

    val quadruple = SeparatedList(listOf("1", "2", "3", "4"), listOf("+", "-", "*"))
    assertEquals("(((1+2)-3)*4)", quadruple.foldLeft { l, s, r -> "($l$s$r)" })
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

    val quadruple = SeparatedList(listOf("1", "2", "3", "4"), listOf("+", "-", "*"))
    assertEquals("(1+(2-(3*4)))", quadruple.foldRight { l, s, r -> "($l$s$r)" })
  }

  @Test
  fun test_separated_large() {
    val count = 10_000
    val input = (1..count).joinToString(",") { "1" }
    val parser = digit().starSeparated(char(','))
    val expectedElements = List(count) { '1' }
    val expectedSeparators = List(count - 1) { ',' }
    assertSuccess(parser, input, SeparatedList(expectedElements, expectedSeparators))
  }

  @Test
  fun test_separated_constructor_default_max_and_equality() {
    val p = SeparatedParser(digit(), char(','), 2)
    assertEquals(2, p.min)
    assertEquals(2, p.max)
    assertFalse(p.isEqualTo(digit()))
  }
}
