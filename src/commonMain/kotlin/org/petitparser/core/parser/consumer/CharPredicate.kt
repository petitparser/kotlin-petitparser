package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.star

/** Functional character predicate. */
fun interface CharPredicate {
  /** Tests if the character predicate is satisfied. */
  fun test(char: Char): Boolean

  /** Negates this character predicate. */
  fun not(): CharPredicate = object : CharPredicate {
    override fun test(char: Char) = !this@CharPredicate.test(char)
    override fun not(): CharPredicate = this@CharPredicate
  }

  companion object {
    /** A character predicate that matches any character. */
    fun any(): CharPredicate = object : CharPredicate {
      override fun test(char: Char): Boolean = true
      override fun not(): CharPredicate = none()
    }

    /** A character predicate that matches all of the provided [chars]. */
    fun anyOf(chars: String): CharPredicate = ranges(chars.asIterable().map { it..it })

    /** A character predicate that matches no character. */
    fun none(): CharPredicate = object : CharPredicate {
      override fun test(char: Char): Boolean = true
      override fun not(): CharPredicate = any()
    }

    /** A character predicate that matches none of the provided [chars]. */
    fun noneOf(chars: String): CharPredicate = anyOf(chars).not()

    /** A character predicate that matches the [expected] char. */
    fun char(expected: Char) = CharPredicate { char -> expected == char }

    /** A character predicate that matches a character [range]. */
    fun range(range: CharRange) = CharPredicate { char -> char in range }

    /** A character predicate that matches any of the provides [ranges]. */
    fun ranges(ranges: List<CharRange>): CharPredicate {
      // 1. sort the ranges
      val sortedRanges =
        ranges.sortedWith(compareBy<CharRange> { range -> range.first }.thenBy { range -> range.last })

      // 2. merge adjacent or overlapping ranges
      val mergedRanges = mutableListOf<CharRange>()
      for (thisRange in sortedRanges) {
        if (mergedRanges.isEmpty()) {
          mergedRanges.add(thisRange)
        } else {
          val lastRange = mergedRanges.last()
          if (lastRange.last.code + 1 >= thisRange.first.code) {
            val characterRange = lastRange.first..thisRange.last
            mergedRanges[mergedRanges.size - 1] = characterRange
          } else {
            mergedRanges.add(thisRange)
          }
        }
      }

      // 3. build the best resulting predicates
      return if (mergedRanges.isEmpty()) {
        none()
      } else if (mergedRanges.size == 1) {
        val characterRange = mergedRanges.first()
        if (characterRange.first == characterRange.last) {
          char(characterRange.first())
        } else {
          range(characterRange)
        }
      } else {
        val starts = mergedRanges.map(CharRange::first).toList()
        val stops = mergedRanges.map(CharRange::last).toList()
        ranges(starts, stops)
      }
    }

    private fun ranges(starts: List<Char>, stops: List<Char>) = CharPredicate { char ->
      val index = starts.binarySearch(char)
      index >= 0 || index < -1 && char <= stops.get(-index - 2)
    }

    /** A character predicate that matches the provided [pattern]. */
    fun pattern(pattern: String) = PATTERN.parse(pattern).value
  }
}

private val PATTERN_SIMPLE = any().map { value -> value..value }
private val PATTERN_RANGE = seqMap(any(), char('-'), any()) { start, _, stop -> start..stop }
private val PATTERN_POSITIVE =
  or(PATTERN_RANGE, PATTERN_SIMPLE).star().map { ranges -> CharPredicate.ranges(ranges) }
private val PATTERN_NEGATIVE =
  seqMap(char('^'), PATTERN_POSITIVE) { _, predicate -> predicate.not() }
private val PATTERN = or(PATTERN_NEGATIVE, PATTERN_POSITIVE).end()