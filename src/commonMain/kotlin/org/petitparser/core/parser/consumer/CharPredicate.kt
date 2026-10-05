package org.petitparser.core.parser.consumer

import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.star

/** Functional character predicate. */
fun interface CharPredicate {
  /** Tests if the character satisfies the predicate. */
  fun test(char: Char): Boolean

  /** Tests if the Unicode code point satisfies the predicate. */
  fun test(code: Int): Boolean = if (code in 0..0xffff) test(code.toChar()) else false

  /** Negates this character predicate. */
  fun not(): CharPredicate = NotCharPredicate(this)

  companion object {
    /** A character predicate that matches any character. */
    fun any(): CharPredicate = ConstantCharPredicate.ANY

    /** A character predicate that matches no character. */
    fun none(): CharPredicate = ConstantCharPredicate.NONE

    /** A character predicate that matches the [expected] char. */
    fun char(expected: Char): CharPredicate = SingleCharPredicate(expected.code)

    /** A character predicate that matches the [expected] code point. */
    fun char(expected: Int): CharPredicate = SingleCharPredicate(expected)

    /** A character predicate that matches a character [range]. */
    fun range(range: CharRange): CharPredicate = RangeCharPredicate(range.first.code, range.last.code)

    /** A character predicate that matches a character range from [start] to [stop]. */
    fun range(start: Char, stop: Char): CharPredicate = RangeCharPredicate(start.code, stop.code)

    /** A character predicate that matches a code point range from [start] to [stop]. */
    fun range(start: Int, stop: Int): CharPredicate = RangeCharPredicate(start, stop)

    /** A character predicate that matches any of the provided [chars]. */
    fun anyOf(
      chars: String,
      ignoreCase: Boolean = false,
      unicode: Boolean = false,
    ): CharPredicate {
      val codePoints = chars.toCodePoints(unicode)
      val ranges = codePoints.map { RangeCharPredicate(it, it) }
      val expanded = if (ignoreCase) expandCase(ranges, unicode) else ranges
      return ranges(expanded, unicode = unicode)
    }

    /** A character predicate that matches none of the provided [chars]. */
    fun noneOf(
      chars: String,
      ignoreCase: Boolean = false,
      unicode: Boolean = false,
    ): CharPredicate = anyOf(chars, ignoreCase, unicode).not()

    /** A character predicate that matches any digit character `'0'..'9'`. */
    fun digit(): CharPredicate = DigitCharPredicate

    /** A character predicate that matches any letter character `'a'..'z'` or `'A'..'Z'`. */
    fun letter(): CharPredicate = LetterCharPredicate

    /** A character predicate that matches any word character `[a-zA-Z0-9_]`. */
    fun word(): CharPredicate = WordCharPredicate

    /** A character predicate that matches any lowercase letter character `'a'..'z'`. */
    fun lowercase(): CharPredicate = LowercaseCharPredicate

    /** A character predicate that matches any uppercase letter character `'A'..'Z'`. */
    fun uppercase(): CharPredicate = UppercaseCharPredicate

    /** A character predicate that matches any whitespace character. */
    fun whitespace(): CharPredicate = WhitespaceCharPredicate

    /** A character predicate that matches any of the provided [ranges]. */
    fun ranges(ranges: List<CharRange>): CharPredicate =
      ranges(ranges.map { RangeCharPredicate(it) }, unicode = false)

    /** A character predicate that optimizes a list of range predicates. */
    fun ranges(ranges: Iterable<RangeCharPredicate>, unicode: Boolean = false): CharPredicate {
      // 1. sort the ranges
      val sortedRanges = ranges.sortedWith(RANGE_COMPARATOR)
      // 2. merge adjacent or overlapping ranges
      val mergedRanges = mutableListOf<RangeCharPredicate>()
      for (thisRange in sortedRanges) {
        if (mergedRanges.isEmpty()) {
          mergedRanges.add(thisRange)
        } else {
          val lastRange = mergedRanges.last()
          if (lastRange.stop + 1 >= thisRange.start) {
            val characterRange = RangeCharPredicate(
              lastRange.start,
              maxOf(lastRange.stop, thisRange.stop),
            )
            mergedRanges[mergedRanges.size - 1] = characterRange
          } else {
            mergedRanges.add(thisRange)
          }
        }
      }
      // 3. build the best resulting predicates
      if (mergedRanges.isEmpty()) {
        return none()
      } else if (mergedRanges.size == 1) {
        val range = mergedRanges.first()
        val maxCode = if (unicode) 0x10ffff else 0xffff
        return if (range.start <= 0 && range.stop >= maxCode) {
          any()
        } else if (range.start == range.stop) {
          char(range.start)
        } else {
          range
        }
      } else {
        val lookupBytes = (mergedRanges.last().stop - mergedRanges.first().start + 32) shr 3
        val rangesBytes = mergedRanges.size * 8
        return if (lookupBytes > 1024 && rangesBytes < (lookupBytes shr 3)) {
          RangesCharPredicate.fromRanges(mergedRanges)
        } else {
          LookupCharPredicate.fromRanges(mergedRanges)
        }
      }
    }

    /** A character predicate that matches the provided [pattern]. */
    fun pattern(
      pattern: String,
      ignoreCase: Boolean = false,
      unicode: Boolean = false,
    ): CharPredicate {
      val isNegated = pattern.startsWith('^')
      val content = if (isNegated) pattern.substring(1) else pattern
      val parser = if (unicode) PATTERN_UNICODE_PARSER else PATTERN_PARSER
      val parsedRanges = parser.parse(content).value
      val ranges = if (ignoreCase) expandCase(parsedRanges, unicode) else parsedRanges
      val predicate = ranges(ranges, unicode = unicode)
      return if (isNegated) predicate.not() else predicate
    }
  }
}

/** Predicate that always returns a constant boolean [value]. */
class ConstantCharPredicate(val value: Boolean) : CharPredicate {
  override fun test(char: Char): Boolean = value
  override fun test(code: Int): Boolean = value
  override fun not(): CharPredicate = if (value) NONE else ANY
  override fun equals(other: Any?): Boolean = other is ConstantCharPredicate && value == other.value
  override fun hashCode(): Int = value.hashCode()
  override fun toString(): String = "ConstantCharPredicate($value)"

  companion object {
    val ANY = ConstantCharPredicate(true)
    val NONE = ConstantCharPredicate(false)
  }
}

/** Predicate matching a single character or code point [value]. */
class SingleCharPredicate(val value: Int) : CharPredicate {
  constructor(char: Char) : this(char.code)

  override fun test(char: Char): Boolean = char.code == value
  override fun test(code: Int): Boolean = code == value
  override fun equals(other: Any?): Boolean = other is SingleCharPredicate && value == other.value
  override fun hashCode(): Int = value.hashCode()
  override fun toString(): String = "SingleCharPredicate($value)"
}

/** Predicate matching character codes between [start] and [stop] inclusive. */
class RangeCharPredicate(val start: Int, val stop: Int) : CharPredicate {
  init {
    require(start <= stop) { "Invalid range: $start-$stop" }
  }

  constructor(range: CharRange) : this(range.first.code, range.last.code)
  constructor(start: Char, stop: Char) : this(start.code, stop.code)

  override fun test(char: Char): Boolean = char.code in start..stop
  override fun test(code: Int): Boolean = code in start..stop
  override fun equals(other: Any?): Boolean =
    other is RangeCharPredicate && start == other.start && stop == other.stop
  override fun hashCode(): Int = 31 * start + stop
  override fun toString(): String = "RangeCharPredicate($start, $stop)"
}

/** Predicate performing O(1) bitset lookup on compact character code spans. */
class LookupCharPredicate(val start: Int, val stop: Int, val bits: IntArray) : CharPredicate {
  override fun test(char: Char): Boolean = test(char.code)
  override fun test(code: Int): Boolean =
    code in start..stop && ((bits[(code - start) shr 5] and (1 shl ((code - start) and 31))) != 0)

  override fun equals(other: Any?): Boolean =
    other is LookupCharPredicate && start == other.start && stop == other.stop && bits.contentEquals(other.bits)

  override fun hashCode(): Int = 31 * (31 * start + stop) + bits.contentHashCode()
  override fun toString(): String = "LookupCharPredicate($start, $stop, ${bits.contentToString()})"

  companion object {
    fun fromRanges(ranges: List<RangeCharPredicate>): LookupCharPredicate {
      require(ranges.isNotEmpty()) { "ranges must not be empty" }
      val start = ranges.first().start
      val stop = ranges.last().stop
      val size = (stop - start + 32) shr 5
      val bits = IntArray(size)
      for (range in ranges) {
        val rangeStart = range.start - start
        val rangeStop = range.stop - start
        for (index in rangeStart..rangeStop) {
          bits[index shr 5] = bits[index shr 5] or (1 shl (index and 31))
        }
      }
      return LookupCharPredicate(start, stop, bits)
    }
  }
}

/** Predicate performing O(log N) binary search on flat ranges. */
class RangesCharPredicate(val ranges: IntArray) : CharPredicate {
  init {
    require(ranges.size % 2 == 0) { "ranges array must have an even length" }
  }

  override fun test(char: Char): Boolean = test(char.code)

  override fun test(code: Int): Boolean {
    var min = 0
    var max = ranges.size - 2
    while (min <= max) {
      val mid = (min + ((max - min) shr 1)) and 1.inv()
      if (ranges[mid] <= code && code <= ranges[mid + 1]) {
        return true
      } else if (code < ranges[mid]) {
        max = mid - 2
      } else {
        min = mid + 2
      }
    }
    return false
  }

  override fun equals(other: Any?): Boolean =
    other is RangesCharPredicate && ranges.contentEquals(other.ranges)

  override fun hashCode(): Int = ranges.contentHashCode()
  override fun toString(): String = "RangesCharPredicate(${ranges.contentToString()})"

  companion object {
    fun fromRanges(ranges: List<RangeCharPredicate>): RangesCharPredicate {
      val array = IntArray(ranges.size * 2)
      var i = 0
      for (range in ranges) {
        array[i++] = range.start
        array[i++] = range.stop
      }
      return RangesCharPredicate(array)
    }
  }
}

/** Inverts the result of [predicate]. */
class NotCharPredicate(val predicate: CharPredicate) : CharPredicate {
  override fun test(char: Char): Boolean = !predicate.test(char)
  override fun test(code: Int): Boolean = !predicate.test(code)
  override fun not(): CharPredicate = predicate
  override fun equals(other: Any?): Boolean =
    other is NotCharPredicate && predicate == other.predicate
  override fun hashCode(): Int = predicate.hashCode().inv()
  override fun toString(): String = "NotCharPredicate($predicate)"
}

/** Predicate matching ASCII digit `'0'..'9'`. */
object DigitCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean = char in '0'..'9'
  override fun test(code: Int): Boolean = code in 48..57
  override fun equals(other: Any?): Boolean = other is DigitCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "DigitCharPredicate"
}

/** Predicate matching ASCII letter `'a'..'z'` or `'A'..'Z'`. */
object LetterCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean =
    (char in 'a'..'z') || (char in 'A'..'Z')
  override fun test(code: Int): Boolean =
    (code in 97..122) || (code in 65..90)
  override fun equals(other: Any?): Boolean = other is LetterCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "LetterCharPredicate"
}

/** Predicate matching ASCII word character `[a-zA-Z0-9_]`. */
object WordCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean =
    (char in 'a'..'z') || (char in 'A'..'Z') || (char in '0'..'9') || char == '_'
  override fun test(code: Int): Boolean =
    (code in 97..122) || (code in 65..90) || (code in 48..57) || code == 95
  override fun equals(other: Any?): Boolean = other is WordCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "WordCharPredicate"
}

/** Predicate matching ASCII lowercase letter `'a'..'z'`. */
object LowercaseCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean = char in 'a'..'z'
  override fun test(code: Int): Boolean = code in 97..122
  override fun equals(other: Any?): Boolean = other is LowercaseCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "LowercaseCharPredicate"
}

/** Predicate matching ASCII uppercase letter `'A'..'Z'`. */
object UppercaseCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean = char in 'A'..'Z'
  override fun test(code: Int): Boolean = code in 65..90
  override fun equals(other: Any?): Boolean = other is UppercaseCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "UppercaseCharPredicate"
}

/** Predicate matching whitespace characters platform independently. */
object WhitespaceCharPredicate : CharPredicate {
  override fun test(char: Char): Boolean = test(char.code)

  override fun test(code: Int): Boolean {
    if (code < 256) {
      return when (code) {
        0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x20, 0x85, 0xA0 -> true
        else -> false
      }
    }
    return when (code) {
      0x1680,
      0x2000, 0x2001, 0x2002, 0x2003, 0x2004, 0x2005, 0x2006, 0x2007, 0x2008, 0x2009, 0x200A,
      0x2028, 0x2029,
      0x202F,
      0x205F,
      0x3000,
      0xFEFF -> true
      else -> false
    }
  }

  override fun equals(other: Any?): Boolean = other is WhitespaceCharPredicate
  override fun hashCode(): Int = this::class.hashCode()
  override fun toString(): String = "WhitespaceCharPredicate"
}

internal val RANGE_COMPARATOR =
  compareBy<RangeCharPredicate> { range -> range.start }.thenBy { range -> range.stop }

internal fun String.toCodePoints(unicode: Boolean = false): List<Int> {
  if (!unicode) return map { it.code }
  val result = mutableListOf<Int>()
  var i = 0
  while (i < length) {
    val c1 = this[i++]
    if (c1.isHighSurrogate() && i < length) {
      val c2 = this[i]
      if (c2.isLowSurrogate()) {
        i++
        result.add(0x10000 + ((c1.code and 0x3FF) shl 10) + (c2.code and 0x3FF))
        continue
      }
    }
    result.add(c1.code)
  }
  return result
}

internal fun codePointToString(code: Int): String =
  if (code in 0..0xffff) {
    code.toChar().toString()
  } else {
    val high = (0xD800 + ((code - 0x10000) shr 10)).toChar()
    val low = (0xDC00 + ((code - 0x10000) and 0x3FF)).toChar()
    "$high$low"
  }

internal fun expandCase(
  ranges: Iterable<RangeCharPredicate>,
  unicode: Boolean,
): List<RangeCharPredicate> {
  val result = mutableListOf<RangeCharPredicate>()
  val maxCode = if (unicode) 0x10ffff else 0xffff
  for (range in ranges) {
    result.add(range)
    if (range.start <= 0 && range.stop >= maxCode) {
      continue
    }
    for (code in range.start..range.stop) {
      val str = codePointToString(code)
      val lower = str.lowercase()
      val upper = str.uppercase()
      val lowerCodes = lower.toCodePoints(unicode)
      if (lower != str && lowerCodes.size == 1) {
        result.add(RangeCharPredicate(lowerCodes[0], lowerCodes[0]))
      }
      val upperCodes = upper.toCodePoints(unicode)
      if (upper != str && upperCodes.size == 1) {
        result.add(RangeCharPredicate(upperCodes[0], upperCodes[0]))
      }
    }
  }
  return result
}

private val PATTERN_PARSER by lazy {
  val single = any().map { RangeCharPredicate(it.code, it.code) }
  val range = seqMap(any(), char('-'), any()) { start, _, stop ->
    require(start <= stop) { "Invalid range: $start-$stop" }
    RangeCharPredicate(start.code, stop.code)
  }
  or(range, single).star().end()
}

private val PATTERN_UNICODE_PARSER by lazy {
  val character = anyUnicode().map {
    val codePoints = it.toCodePoints(unicode = true)
    codePoints[0]
  }
  val single = character.map { RangeCharPredicate(it, it) }
  val range = seqMap(character, char('-'), character) { start, _, stop ->
    require(start <= stop) { "Invalid range: $start-$stop" }
    RangeCharPredicate(start, stop)
  }
  or(range, single).star().end()
}