package org.petitparser.core.context

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class TokenTest {
  @Test
  fun test_token_properties() {
    val token = Token("val", "hello val world", 6, 9)
    assertEquals("val", token.value)
    assertEquals("hello val world", token.buffer)
    assertEquals(6, token.start)
    assertEquals(9, token.stop)
    assertEquals("val", token.input)
    assertEquals(3, token.length)
    assertEquals(1, token.line)
    assertEquals(7, token.column)
    assertEquals("Token[1:7]: val", token.toString())
  }

  @Test
  fun test_token_join() {
    val t1 = Token('a', "abc", 0, 1)
    val t2 = Token('b', "abc", 1, 2)
    val joined = listOf(t1, t2).join()
    assertEquals(listOf('a', 'b'), joined.value)
    assertEquals(0, joined.start)
    assertEquals(2, joined.stop)

    val joinedVararg = Token.join(t1, t2)
    assertEquals(listOf('a', 'b'), joinedVararg.value)

    assertFailsWith<IllegalArgumentException> {
      Token.join(emptyList<Token<Char>>())
    }

    assertFailsWith<IllegalArgumentException> {
      Token.join(t1, Token('b', "diff", 0, 1))
    }
  }

  @Test
  fun test_token_equality() {
    val t1 = Token("a", "abc", 0, 1)
    val t2 = Token("a", "abc", 0, 1)
    val t3 = Token("b", "abc", 1, 2)
    assertEquals(t1, t2)
    assertEquals(t1.hashCode(), t2.hashCode())
    assertNotEquals(t1, t3)
  }
}
