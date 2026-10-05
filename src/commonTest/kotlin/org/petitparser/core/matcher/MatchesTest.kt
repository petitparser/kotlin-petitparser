package org.petitparser.core.matcher

import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.repeater.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MatchesTest {
  @Test
  fun test_accept() {
    val parser = string("foo")
    assertTrue(parser.accept("foo"))
    assertTrue(parser.accept("foobar"))
    assertTrue(parser.accept("barfoo", start = 3))
    assertFalse(parser.accept("bar"))
    assertFalse(parser.accept("barfoo"))
  }

  @Test
  fun test_matches_non_overlapping() {
    val parser = letter().plus().flatten()
    val matches = parser.matches("abc def ghi").toList()
    assertEquals(listOf("abc", "def", "ghi"), matches)
  }

  @Test
  fun test_matches_overlapping() {
    val parser = letter().plus().flatten()
    val matches = parser.matches("abc", overlapping = true).toList()
    assertEquals(listOf("abc", "bc", "c"), matches)
  }

  @Test
  fun test_matches_skipping() {
    val parser = letter().plus().flatten()
    val matches = parser.matchesSkipping("abc def").toList()
    assertEquals(listOf("abc", "def"), matches)
  }

  @Test
  fun test_pattern_match_as_prefix() {
    val pattern = string("foo").toPattern()
    val match = pattern.matchAsPrefix("foobar")
    assertNotNull(match)
    assertEquals(0, match.start)
    assertEquals(3, match.end)
    assertEquals("foo", match.group)

    val noMatch = pattern.matchAsPrefix("barfoo")
    assertNull(noMatch)
  }

  @Test
  fun test_pattern_all_matches() {
    val pattern = string("ab").toPattern()
    val matches = pattern.allMatches("ab_ab_ab").toList()
    assertEquals(3, matches.size)
    assertEquals(listOf(0, 3, 6), matches.map { it.start })
    assertEquals(listOf(2, 5, 8), matches.map { it.end })
    assertEquals(listOf("ab", "ab", "ab"), matches.map { it.group })
  }
}
