package org.petitparser.core.parser.action

import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.times
import kotlin.test.Test

internal class CastListTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().star().castList<String>())
  }

  @Test
  fun test_castList() {
    val parser = digit().map(Char::digitToInt).times(3).castList<Number>()
    assertSuccess(parser, "123", listOf<Number>(1, 2, 3))
    assertFailure(parser, "abc", "digit expected", 0)
    assertFailure(parser, "", "digit expected", 0)
  }

  @Test
  fun test_castList_empty() {
    val parser = digit().star().castList<Number>()
    assertSuccess(parser, "", emptyList<Number>())
  }

  @Test
  fun test_castList_nonList() {
    val parser = digit().castList<Int>()
    kotlin.test.assertFailsWith<ClassCastException> {
      parser.parse("1").value
    }
  }

  @Test
  fun test_equality() {
    val p1 = digit().star().castList<Number>()
    val p2 = digit().star().castList<Number>()
    val p3 = digit().constant(listOf(1)).castList<Number>()
    kotlin.test.assertTrue(p1.isEqualTo(p2))
    kotlin.test.assertFalse(p1.isEqualTo(p3))
  }
}
