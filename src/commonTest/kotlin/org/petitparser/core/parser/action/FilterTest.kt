package org.petitparser.core.parser.action

import org.petitparser.core.context.failure
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FilterTest {
  @Test
  fun test_invariants() {
    val predicate: (Char) -> Boolean = { true }
    expectParserInvariants(any().filter(predicate))
  }

  @Test
  fun test_default() {
    val parser = any().filter { it == '*' }
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "!", "unexpected '!'", 0)
  }

  @Test
  fun test_with_message() {
    val parser = any().filter({ it == '*' }, message = "star expected")
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "!", "star expected", 0)
  }

  @Test
  fun test_with_message_leading() {
    val parser = any().filter("star expected") { it == '*' }
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "!", "star expected", 0)
  }

  @Test
  fun test_with_message_callback() {
    val parser = any().filter({ it == '*' }, message = { "char '$it' is not a star" })
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "!", "char '!' is not a star", 0)
  }

  @Test
  fun test_with_factory() {
    val parser = digit().plus().flatten().map(String::toInt).filter(
      predicate = { it % 7 == 0 },
      factory = { input, success -> input.failure("${success.value} is not divisible by 7") },
    )
    assertSuccess(parser, "7", 7)
    assertSuccess(parser, "14", 14)
    assertSuccess(parser, "861", 861)
    assertFailure(parser, "", "digit expected", 0)
    assertFailure(parser, "865", "865 is not divisible by 7", 0)
  }

  @Test
  fun test_filter_collection() {
    val parser = any().plus().filter({ it.first() == it.last() })
    assertSuccess(parser, "a", listOf('a'))
    assertSuccess(parser, "aa", listOf('a', 'a'))
    assertSuccess(parser, "aba", listOf('a', 'b', 'a'))
    assertSuccess(parser, "abba", listOf('a', 'b', 'b', 'a'))
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "ab", "unexpected '[a, b]'", 0)
    assertFailure(parser, "abc", "unexpected '[a, b, c]'", 0)
  }

  @Test
  fun test_filter_custom_failure_factory() {
    val parser = any().plus().filter({ it.first() == it.last() }, { input, success ->
      input.failure(
        "${success.value.first()} != ${success.value.last()}",
        success.position - 1,
      )
    })
    assertSuccess(parser, "a", listOf('a'))
    assertSuccess(parser, "aa", listOf('a', 'a'))
    assertSuccess(parser, "aba", listOf('a', 'b', 'a'))
    assertSuccess(parser, "abba", listOf('a', 'b', 'b', 'a'))
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "ab", "a != b", 1)
    assertFailure(parser, "abc", "a != c", 2)
  }

  @Test
  fun test_filter_factory_named() {
    val factory: FailureFactory<Char> = { input, _ -> input.failure("failed") }
    val parser = any().filter(predicate = { it == '*' }, factory = factory)
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "!", "failed", 0)
  }

  @Test
  fun test_equality() {
    val pred1: (Char) -> Boolean = { it == '*' }
    val pred2: (Char) -> Boolean = { it == '#' }
    val p1 = any().filter(pred1)
    val p2 = any().filter(pred1)
    val p3 = any().filter(pred2)
    val pMsg1 = any().filter(pred1, message = "star")
    val pMsg2 = any().filter(pred1, message = "star")
    val pMsg3 = any().filter(pred1, message = "hash")
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertTrue(pMsg1.isEqualTo(pMsg2))
    assertFalse(pMsg1.isEqualTo(pMsg3))
    assertFalse(p1.isEqualTo(pMsg1))
    assertFalse(p1.isEqualTo(any()))
  }

  @Test
  fun test_failure_factory_property() {
    val parser = any().filter { true } as FilterParser<Char>
    assertEquals(parser.factory, parser.failureFactory)
  }
}
