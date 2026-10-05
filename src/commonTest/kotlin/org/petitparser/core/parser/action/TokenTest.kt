package org.petitparser.core.parser.action

import org.petitparser.core.context.Token
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.expectParserInvariants
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TokenTest {
  @Test
  fun test_invariants() {
    expectParserInvariants(any().token())
  }

  @Test
  fun test_token() {
    val parser = digit().plus().token()
    assertFailure(parser, "", "digit expected")
    assertFailure(parser, "a", "digit expected")
    val token = parser.parse("123").value
    assertEquals(listOf('1', '2', '3'), token.value)
    assertEquals("123", token.buffer)
    assertEquals(0, token.start)
    assertEquals(3, token.stop)
    assertEquals("123", token.input)
    assertEquals(3, token.length)
    assertEquals(1, token.line)
    assertEquals(1, token.column)
    assertEquals("Token[1:1]: [1, 2, 3]", token.toString())
  }

  @Test
  fun test_token_data() {
    val input = "1\r12\r\n123\n1234"
    val parser = any().map(Char::code).token().star()
    val result = parser.parse(input).value
    assertContentEquals(
      listOf(
        49, 13, 49, 50, 13, 10, 49, 50, 51, 10, 49, 50, 51, 52
      ),
      result.map(Token<Int>::value),
    )
    assertContentEquals(
      listOf(
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input,
        input
      ),
      result.map(Token<Int>::buffer),
    )
    assertContentEquals(
      listOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
      ),
      result.map(Token<Int>::start),
    )
    assertContentEquals(
      listOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14
      ),
      result.map(Token<Int>::stop),
    )
    assertContentEquals(
      listOf(
        "1", "\r", "1", "2", "\r", "\n", "1", "2", "3", "\n", "1", "2", "3", "4"
      ),
      result.map(Token<Int>::input),
    )
    assertContentEquals(
      listOf(
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
      ),
      result.map(Token<Int>::length),
    )
    assertContentEquals(
      listOf(
        1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4
      ),
      result.map(Token<Int>::line),
    )
    assertContentEquals(
      listOf(
        1, 2, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4
      ),
      result.map(Token<Int>::column),
    )
  }

  @Test
  fun test_join() {
    val t1 = Token('a', "abc", 0, 1)
    val t2 = Token('b', "abc", 1, 2)
    val t3 = Token('c', "abc", 2, 3)

    val joined = Token.join(listOf(t1, t2, t3))
    assertEquals(listOf('a', 'b', 'c'), joined.value)
    assertEquals("abc", joined.buffer)
    assertEquals(0, joined.start)
    assertEquals(3, joined.stop)

    val reverseJoined = Token.join(listOf(t3, t2, t1))
    assertEquals(listOf('c', 'b', 'a'), reverseJoined.value)
    assertEquals("abc", reverseJoined.buffer)
    assertEquals(0, reverseJoined.start)
    assertEquals(3, reverseJoined.stop)

    kotlin.test.assertFailsWith<IllegalArgumentException> {
      Token.join(emptyList<Token<Char>>())
    }

    val tDiff = Token('x', "xyz", 0, 1)
    kotlin.test.assertFailsWith<IllegalArgumentException> {
      Token.join(listOf(t1, tDiff))
    }
  }

  @Test
  fun test_token_equality() {
    val t1 = Token("a", "abc", 0, 1)
    val t2 = Token("a", "abc", 0, 1)
    val t3 = Token("b", "abc", 1, 2)
    assertEquals(t1, t2)
    assertEquals(t1.hashCode(), t2.hashCode())
    kotlin.test.assertNotEquals(t1, t3)
  }

  @Test
  fun test_equality() {
    val p1 = digit().token()
    val p2 = digit().token()
    assertTrue(p1.isEqualTo(p2))
  }
}
