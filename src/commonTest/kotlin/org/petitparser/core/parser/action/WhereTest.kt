package org.petitparser.core.parser.action

import org.petitparser.core.context.failure
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class WhereTest {
  @Test
  fun test_invariants() {
    val predicate: (Char) -> Boolean = { true }
    expectParserInvariants(any().where(predicate))
  }

  @Test
  fun test_default() {
    val parser = any().where { it == '*' }
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "!", "unexpected '!'", 0)
  }

  @Test
  fun test_with_message() {
    val parser = any().where({ it == '*' }, message = "star expected")
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "", "input expected", 0)
    assertFailure(parser, "!", "star expected", 0)
  }

  @Test
  fun test_with_factory() {
    val parser = digit().plus().flatten().map(String::toInt).where(
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
  fun test_filter() {
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
  fun test_filter_failureFactory() {
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
  fun test_filter_message() {
    val p1 = any().filter({ it == '*' }, message = "star expected")
    assertSuccess(p1, "*", '*')
    assertFailure(p1, "!", "star expected", 0)

    val p2 = any().filter("star expected") { it == '*' }
    assertSuccess(p2, "*", '*')
    assertFailure(p2, "!", "star expected", 0)
  }

  @Test
  fun test_filter_message_callback() {
    val p = any().filter({ it == '*' }, message = { "char '$it' is not a star" })
    assertSuccess(p, "*", '*')
    assertFailure(p, "!", "char '!' is not a star", 0)
  }

  @Test
  fun test_where_message_callback() {
    val p = any().where({ it == '*' }, message = { "char '$it' is not a star" })
    assertSuccess(p, "*", '*')
    assertFailure(p, "!", "char '!' is not a star", 0)
  }

  @Test
  fun test_where_message_leading() {
    val parser = any().where("star expected") { it == '*' }
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "!", "star expected", 0)
  }

  @Test
  fun test_where_factory_named() {
    val factory: FailureFactory<Char> = { input, _ -> input.failure("failed") }
    val parser = any().where(predicate = { it == '*' }, factory = factory)
    assertSuccess(parser, "*", '*')
    assertFailure(parser, "!", "failed", 0)

    val filterParser = any().filter(predicate = { it == '*' }, factory = factory)
    assertSuccess(filterParser, "*", '*')
    assertFailure(filterParser, "!", "failed", 0)
  }

  @Test
  fun test_equality() {
    val pred1: (Char) -> Boolean = { it == '*' }
    val pred2: (Char) -> Boolean = { it == '#' }
    val p1 = any().where(pred1)
    val p2 = any().where(pred1)
    val p3 = any().where(pred2)
    val pMsg1 = any().where(pred1, message = "star")
    val pMsg2 = any().where(pred1, message = "star")
    val pMsg3 = any().where(pred1, message = "hash")
    assertTrue(p1.isEqualTo(p2))
    assertFalse(p1.isEqualTo(p3))
    assertTrue(pMsg1.isEqualTo(pMsg2))
    assertFalse(pMsg1.isEqualTo(pMsg3))
    assertFalse(p1.isEqualTo(pMsg1))
  }
}
