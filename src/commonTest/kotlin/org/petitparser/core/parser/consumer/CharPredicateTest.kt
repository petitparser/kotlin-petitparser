package org.petitparser.core.parser.consumer

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CharPredicateTest {
  @Test
  fun test_constant_any() {
    val any = CharPredicate.any()
    assertTrue(any.test('a'))
    assertTrue(any.test('0'))
    assertTrue(any.test(0x10ffff))
    assertEquals(any, ConstantCharPredicate(true))
    assertEquals(any, any)
    assertNotEquals(any, CharPredicate.none())
    assertNotEquals<Any?>(any, null)
    assertNotEquals<Any?>(any, "any")
    assertEquals(any.hashCode(), ConstantCharPredicate(true).hashCode())
    assertEquals("ConstantCharPredicate(true)", any.toString())

    val notAny = any.not()
    assertEquals(CharPredicate.none(), notAny)
    assertFalse(notAny.test('a'))
  }

  @Test
  fun test_constant_none() {
    val none = CharPredicate.none()
    assertFalse(none.test('a'))
    assertFalse(none.test('0'))
    assertFalse(none.test(0x10ffff))
    assertEquals(none, ConstantCharPredicate(false))
    assertEquals(none, none)
    assertNotEquals(none, CharPredicate.any())
    assertNotEquals<Any?>(none, null)
    assertEquals(none.hashCode(), ConstantCharPredicate(false).hashCode())
    assertEquals("ConstantCharPredicate(false)", none.toString())

    val notNone = none.not()
    assertEquals(CharPredicate.any(), notNone)
    assertTrue(notNone.test('a'))
  }

  @Test
  fun test_single() {
    val p = SingleCharPredicate('x')
    assertTrue(p.test('x'))
    assertTrue(p.test('x'.code))
    assertFalse(p.test('y'))
    assertFalse(p.test('y'.code))
    assertEquals(p, SingleCharPredicate('x'.code))
    assertNotEquals(p, SingleCharPredicate('y'.code))
    assertNotEquals<Any?>(p, null)
    assertEquals(p.hashCode(), SingleCharPredicate('x').hashCode())
    assertEquals("SingleCharPredicate(120)", p.toString())

    val notP = p.not()
    assertFalse(notP.test('x'))
    assertTrue(notP.test('y'))
    assertEquals(p, notP.not())
  }

  @Test
  fun test_range() {
    val r = RangeCharPredicate('a', 'z')
    assertTrue(r.test('a'))
    assertTrue(r.test('m'))
    assertTrue(r.test('z'))
    assertFalse(r.test('A'))
    assertFalse(r.test('0'))

    val rFromCharRange = RangeCharPredicate('a'..'z')
    assertEquals(r, rFromCharRange)
    assertEquals(r.hashCode(), rFromCharRange.hashCode())
    assertNotEquals(r, RangeCharPredicate('a', 'y'))
    assertNotEquals<Any?>(r, null)
    assertEquals("RangeCharPredicate(97, 122)", r.toString())

    assertFailsWith<IllegalArgumentException> {
      RangeCharPredicate('z', 'a')
    }

    val notR = r.not()
    assertFalse(notR.test('m'))
    assertTrue(notR.test('A'))
    assertEquals(r, notR.not())
  }

  @Test
  fun test_lookup() {
    val ranges = listOf(RangeCharPredicate('0', '9'), RangeCharPredicate('a', 'z'))
    val lookup = LookupCharPredicate.fromRanges(ranges)
    assertTrue(lookup.test('0'))
    assertTrue(lookup.test('9'))
    assertTrue(lookup.test('a'))
    assertTrue(lookup.test('z'))
    assertFalse(lookup.test('/'))
    assertFalse(lookup.test(':'))
    assertFalse(lookup.test('`'))
    assertFalse(lookup.test('{'))
    assertFalse(lookup.test('A'))
    assertFalse(lookup.test(0x10000))

    val copy = LookupCharPredicate(lookup.start, lookup.stop, lookup.bits.copyOf())
    assertEquals(lookup, copy)
    assertEquals(lookup.hashCode(), copy.hashCode())
    assertNotEquals<Any?>(lookup, null)
    assertTrue(lookup.toString().startsWith("LookupCharPredicate(48, 122,"))

    assertFailsWith<IllegalArgumentException> {
      LookupCharPredicate.fromRanges(emptyList())
    }
  }

  @Test
  fun test_ranges() {
    val ranges = listOf(
      RangeCharPredicate(10, 20),
      RangeCharPredicate(30, 40),
      RangeCharPredicate(50, 60),
    )
    val r = RangesCharPredicate.fromRanges(ranges)
    assertFalse(r.test(5))
    assertTrue(r.test(10))
    assertTrue(r.test(15))
    assertTrue(r.test(20))
    assertFalse(r.test(25))
    assertTrue(r.test(30))
    assertTrue(r.test(35))
    assertTrue(r.test(40))
    assertFalse(r.test(45))
    assertTrue(r.test(50))
    assertTrue(r.test(55))
    assertTrue(r.test(60))
    assertFalse(r.test(65))

    val copy = RangesCharPredicate(r.ranges.copyOf())
    assertEquals(r, copy)
    assertEquals(r.hashCode(), copy.hashCode())
    assertNotEquals<Any?>(r, null)
    assertTrue(r.toString().startsWith("RangesCharPredicate("))

    assertFailsWith<IllegalArgumentException> {
      RangesCharPredicate(intArrayOf(1, 2, 3))
    }
  }

  @Test
  fun test_not_predicate() {
    val inner = DigitCharPredicate
    val notDigit = NotCharPredicate(inner)
    assertFalse(notDigit.test('5'))
    assertFalse(notDigit.test('5'.code))
    assertTrue(notDigit.test('a'))
    assertTrue(notDigit.test('a'.code))
    assertEquals(inner, notDigit.not())
    assertEquals(notDigit, NotCharPredicate(inner))
    assertNotEquals<Any?>(notDigit, null)
    assertNotEquals(notDigit, NotCharPredicate(LetterCharPredicate))
    assertEquals(notDigit.hashCode(), NotCharPredicate(inner).hashCode())
    assertEquals("NotCharPredicate(DigitCharPredicate)", notDigit.toString())
  }

  @Test
  fun test_built_in_predicates() {
    val digit = CharPredicate.digit()
    assertTrue(digit.test('0'))
    assertTrue(digit.test('9'))
    assertFalse(digit.test('a'))
    assertEquals(digit, DigitCharPredicate)
    assertEquals("DigitCharPredicate", digit.toString())

    val letter = CharPredicate.letter()
    assertTrue(letter.test('a'))
    assertTrue(letter.test('z'))
    assertTrue(letter.test('A'))
    assertTrue(letter.test('Z'))
    assertFalse(letter.test('0'))
    assertFalse(letter.test('_'))
    assertEquals(letter, LetterCharPredicate)
    assertEquals("LetterCharPredicate", letter.toString())

    val word = CharPredicate.word()
    assertTrue(word.test('a'))
    assertTrue(word.test('Z'))
    assertTrue(word.test('5'))
    assertTrue(word.test('_'))
    assertFalse(word.test('-'))
    assertFalse(word.test(' '))
    assertEquals(word, WordCharPredicate)
    assertEquals("WordCharPredicate", word.toString())

    val lower = CharPredicate.lowercase()
    assertTrue(lower.test('a'))
    assertTrue(lower.test('z'))
    assertFalse(lower.test('A'))
    assertFalse(lower.test('0'))
    assertEquals(lower, LowercaseCharPredicate)
    assertEquals("LowercaseCharPredicate", lower.toString())

    val upper = CharPredicate.uppercase()
    assertTrue(upper.test('A'))
    assertTrue(upper.test('Z'))
    assertFalse(upper.test('a'))
    assertFalse(upper.test('0'))
    assertEquals(upper, UppercaseCharPredicate)
    assertEquals("UppercaseCharPredicate", upper.toString())

    val ws = CharPredicate.whitespace()
    assertTrue(ws.test(' '))
    assertTrue(ws.test('\t'))
    assertTrue(ws.test('\n'))
    assertTrue(ws.test('\r'))
    assertTrue(ws.test(0xA0))
    assertTrue(ws.test(0x3000))
    assertTrue(ws.test(0xFEFF))
    assertFalse(ws.test('a'))
    assertFalse(ws.test('0'))
    assertFalse(ws.test(0x10000))
    assertEquals(ws, WhitespaceCharPredicate)
    assertEquals("WhitespaceCharPredicate", ws.toString())
  }

  @Test
  fun test_optimize_empty() {
    val p = CharPredicate.ranges(emptyList<RangeCharPredicate>())
    assertEquals(CharPredicate.none(), p)
  }

  @Test
  fun test_optimize_full_range_without_zero() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(1, 65536)), unicode = false)
    assertFalse(predicate.test(0))
    assertTrue(predicate.test(1))
    assertTrue(predicate.test(65536))
    assertNotEquals(CharPredicate.any(), predicate)
  }

  @Test
  fun test_optimize_disjoint_ranges_summing_to_full_count() {
    val predicate = CharPredicate.ranges(
      listOf(RangeCharPredicate(1, 32768), RangeCharPredicate(32770, 65537)),
      unicode = false,
    )
    assertFalse(predicate.test(0))
    assertFalse(predicate.test(32769))
    assertNotEquals(CharPredicate.any(), predicate)
  }

  @Test
  fun test_optimize_unicode_full_range_without_zero() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(1, 0x10ffff + 1)), unicode = true)
    assertFalse(predicate.test(0))
    assertTrue(predicate.test(1))
    assertNotEquals(CharPredicate.any(), predicate)
  }

  @Test
  fun test_optimize_covers_everything() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(0, 0xffff)), unicode = false)
    assertEquals(CharPredicate.any(), predicate)
  }

  @Test
  fun test_optimize_covers_everything_unicode() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(0, 0x10ffff)), unicode = true)
    assertEquals(CharPredicate.any(), predicate)
  }

  @Test
  fun test_optimize_single_character() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(97, 97)))
    assertTrue(predicate is SingleCharPredicate)
    assertEquals(SingleCharPredicate('a'), predicate)
  }

  @Test
  fun test_optimize_single_range() {
    val predicate = CharPredicate.ranges(listOf(RangeCharPredicate(97, 100)))
    assertTrue(predicate is RangeCharPredicate)
    assertEquals(RangeCharPredicate('a', 'd'), predicate)
  }

  @Test
  fun test_optimize_selects_lookup_for_dense_ranges() {
    val predicate = CharPredicate.ranges(
      listOf(
        RangeCharPredicate(48, 57),
        RangeCharPredicate(65, 90),
        RangeCharPredicate(97, 122),
      ),
      unicode = false,
    )
    assertTrue(predicate is LookupCharPredicate)
    assertTrue(predicate.test('a'.code))
    assertTrue(predicate.test('Z'.code))
    assertTrue(predicate.test('0'.code))
    assertFalse(predicate.test('?'.code))
  }

  @Test
  fun test_optimize_selects_ranges_for_sparse_ranges_with_large_span() {
    val predicate = CharPredicate.ranges(
      listOf(
        RangeCharPredicate(97, 97),
        RangeCharPredicate(0x10000, 0x10000),
      ),
      unicode = true,
    )
    assertTrue(predicate is RangesCharPredicate)
    assertTrue(predicate.test(97))
    assertTrue(predicate.test(0x10000))
    assertFalse(predicate.test(98))
    assertFalse(predicate.test(0))
  }

  @Test
  fun test_optimize_adjacent_and_overlapping() {
    val p1 = CharPredicate.ranges(listOf('c'..'e', 'a'..'c'))
    assertTrue(p1.test('a'))
    assertTrue(p1.test('c'))
    assertTrue(p1.test('e'))
    assertFalse(p1.test('f'))

    val p2 = CharPredicate.ranges(listOf('a'..'e', 'c'..'d'))
    assertTrue(p2.test('a'))
    assertTrue(p2.test('e'))
    assertFalse(p2.test('f'))
  }

  @Test
  fun test_stress_lookup() {
    stress(LookupCharPredicate::fromRanges)
  }

  @Test
  fun test_stress_ranges() {
    stress(RangesCharPredicate::fromRanges)
  }

  private fun stress(
    factory: (List<RangeCharPredicate>) -> CharPredicate,
    repeat: Int = 200,
    size: Int = 1000,
    maxGap: Int = 100,
    maxRange: Int = 100,
    seed: Int = 81728392,
  ) {
    val random = Random(seed)
    for (i in 0 until repeat) {
      var start = random.nextInt(maxGap)
      val ranges = mutableListOf<RangeCharPredicate>()
      val included = BooleanArray(size + 1)
      while (true) {
        val end = start + random.nextInt(maxRange)
        if (end > size) break
        ranges.add(RangeCharPredicate(start, end))
        for (k in start..end) included[k] = true
        start = random.nextInt(maxGap) + end + 1
      }
      if (ranges.isEmpty()) continue
      val predicate = factory(ranges)
      for (k in 0..size) {
        assertEquals(included[k], predicate.test(k), "failure at index $k in iteration $i")
      }
    }
  }

  @Test
  fun test_char_predicate_sam() {
    val vowels = CharPredicate { it in "aeiou" }
    assertTrue(vowels.test('a'))
    assertTrue(vowels.test('e'))
    assertFalse(vowels.test('b'))
    assertTrue(vowels.test('a'.code))
    assertFalse(vowels.test(0x10000))
  }

  @Test
  fun test_anyOf_noneOf() {
    val anyOf = CharPredicate.anyOf("abc")
    assertTrue(anyOf.test('a'))
    assertTrue(anyOf.test('b'))
    assertTrue(anyOf.test('c'))
    assertFalse(anyOf.test('d'))

    val anyOfCase = CharPredicate.anyOf("aB", ignoreCase = true)
    assertTrue(anyOfCase.test('a'))
    assertTrue(anyOfCase.test('A'))
    assertTrue(anyOfCase.test('b'))
    assertTrue(anyOfCase.test('B'))
    assertFalse(anyOfCase.test('c'))

    val noneOf = CharPredicate.noneOf("ab1")
    assertTrue(noneOf.test('c'))
    assertFalse(noneOf.test('a'))
    assertFalse(noneOf.test('1'))

    val unicodeAnyOf = CharPredicate.anyOf("abc\uD83D\uDE00", unicode = true)
    assertTrue(unicodeAnyOf.test('a'.code))
    assertTrue(unicodeAnyOf.test(0x1F600))
    assertFalse(unicodeAnyOf.test('z'.code))
  }

  @Test
  fun test_pattern() {
    val p = CharPredicate.pattern("a-c")
    assertTrue(p.test('a'))
    assertTrue(p.test('b'))
    assertTrue(p.test('c'))
    assertFalse(p.test('d'))

    val pNeg = CharPredicate.pattern("^a-c")
    assertFalse(pNeg.test('a'))
    assertTrue(pNeg.test('d'))

    val pEmpty = CharPredicate.pattern("")
    assertEquals(CharPredicate.none(), pEmpty)

    val pEmptyNeg = CharPredicate.pattern("^")
    assertEquals(CharPredicate.any(), pEmptyNeg)

    val pCase = CharPredicate.pattern("b-d", ignoreCase = true)
    assertTrue(pCase.test('b'))
    assertTrue(pCase.test('B'))
    assertTrue(pCase.test('c'))
    assertTrue(pCase.test('C'))
    assertFalse(pCase.test('a'))

    assertFailsWith<IllegalArgumentException> {
      CharPredicate.pattern("c-a")
    }
  }
}
