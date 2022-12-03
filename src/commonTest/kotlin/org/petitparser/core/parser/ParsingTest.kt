package org.petitparser.core.parser

import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.consumer.digit
import kotlin.test.Test
import kotlin.test.assertContentEquals

val parser = (digit() seq digit()).flatten()
val input = "a123b456"

internal class ParsingTest {
  @Test
  fun test_allMatches() {
    assertContentEquals(
      sequenceOf("12", "45"),
      parser.matches(input),
    )
  }

  @Test
  fun test_allMatches_start() {
    assertContentEquals(
      sequenceOf("45"),
      parser.matches(input, start = 3),
    )
  }

  @Test
  fun test_allMatches_overlapping() {
    assertContentEquals(
      sequenceOf("12", "23", "45", "56"),
      parser.matches(input, overlapping = true),
    )
  }

  @Test
  fun test_allMatches_overlapping_start() {
    assertContentEquals(
      sequenceOf("45", "56"),
      parser.matches(input, overlapping = true, start = 3),
    )
  }
}