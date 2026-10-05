package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Sequence of two parsers [p1] and [p2] with a strongly typed mapping function [block]. */
fun <R1, R2, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  block: (R1, R2) -> R,
): SequenceMapParser2<R1, R2, R> = SequenceMapParser2(p1, p2, block, false)

/** Sequence of two parsers [p1] and [p2] with a strongly typed mapping function [block]. */
fun <R1, R2, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  hasSideEffects: Boolean,
  block: (R1, R2) -> R,
): SequenceMapParser2<R1, R2, R> = SequenceMapParser2(p1, p2, block, hasSideEffects)

/** Sequence of three parsers [p1], ..., [p3] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  block: (R1, R2, R3) -> R,
): SequenceMapParser3<R1, R2, R3, R> = SequenceMapParser3(p1, p2, p3, block, false)

/** Sequence of three parsers [p1], ..., [p3] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3) -> R,
): SequenceMapParser3<R1, R2, R3, R> = SequenceMapParser3(p1, p2, p3, block, hasSideEffects)

/** Sequence of four parsers [p1], ..., [p4] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  block: (R1, R2, R3, R4) -> R,
): SequenceMapParser4<R1, R2, R3, R4, R> = SequenceMapParser4(p1, p2, p3, p4, block, false)

/** Sequence of four parsers [p1], ..., [p4] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4) -> R,
): SequenceMapParser4<R1, R2, R3, R4, R> = SequenceMapParser4(p1, p2, p3, p4, block, hasSideEffects)

/** Sequence of five parsers [p1], ..., [p5] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  block: (R1, R2, R3, R4, R5) -> R,
): SequenceMapParser5<R1, R2, R3, R4, R5, R> = SequenceMapParser5(p1, p2, p3, p4, p5, block, false)

/** Sequence of five parsers [p1], ..., [p5] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4, R5) -> R,
): SequenceMapParser5<R1, R2, R3, R4, R5, R> = SequenceMapParser5(p1, p2, p3, p4, p5, block, hasSideEffects)

/** Sequence of six parsers [p1], ..., [p6] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  block: (R1, R2, R3, R4, R5, R6) -> R,
): SequenceMapParser6<R1, R2, R3, R4, R5, R6, R> =
  SequenceMapParser6(p1, p2, p3, p4, p5, p6, block, false)

/** Sequence of six parsers [p1], ..., [p6] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4, R5, R6) -> R,
): SequenceMapParser6<R1, R2, R3, R4, R5, R6, R> =
  SequenceMapParser6(p1, p2, p3, p4, p5, p6, block, hasSideEffects)

/** Sequence of seven parsers [p1], ..., [p7] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  block: (R1, R2, R3, R4, R5, R6, R7) -> R,
): SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, R> =
  SequenceMapParser7(p1, p2, p3, p4, p5, p6, p7, block, false)

/** Sequence of seven parsers [p1], ..., [p7] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4, R5, R6, R7) -> R,
): SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, R> =
  SequenceMapParser7(p1, p2, p3, p4, p5, p6, p7, block, hasSideEffects)

/** Sequence of eight parsers [p1], ..., [p8] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
  block: (R1, R2, R3, R4, R5, R6, R7, R8) -> R,
): SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, R> =
  SequenceMapParser8(p1, p2, p3, p4, p5, p6, p7, p8, block, false)

/** Sequence of eight parsers [p1], ..., [p8] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4, R5, R6, R7, R8) -> R,
): SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, R> =
  SequenceMapParser8(p1, p2, p3, p4, p5, p6, p7, p8, block, hasSideEffects)

/** Sequence of nine parsers [p1], ..., [p9] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8, R9, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
  p9: Parser<R9>,
  block: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R,
): SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, R> =
  SequenceMapParser9(p1, p2, p3, p4, p5, p6, p7, p8, p9, block, false)

/** Sequence of nine parsers [p1], ..., [p9] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8, R9, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
  p9: Parser<R9>,
  hasSideEffects: Boolean,
  block: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R,
): SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, R> =
  SequenceMapParser9(p1, p2, p3, p4, p5, p6, p7, p8, p9, block, hasSideEffects)

/** A parser that runs 2 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser2<R1, R2, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  val callback: (R1, R2) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    return r2.success(callback(r1.value, r2.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
  }

  override fun copy(): SequenceMapParser2<R1, R2, R> =
    SequenceMapParser2(parser1, parser2, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser2<*, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 3 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser3<R1, R2, R3, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  val callback: (R1, R2, R3) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    return r3.success(callback(r1.value, r2.value, r3.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
  }

  override fun copy(): SequenceMapParser3<R1, R2, R3, R> =
    SequenceMapParser3(parser1, parser2, parser3, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser3<*, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 4 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser4<R1, R2, R3, R4, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  val callback: (R1, R2, R3, R4) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    return r4.success(callback(r1.value, r2.value, r3.value, r4.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
  }

  override fun copy(): SequenceMapParser4<R1, R2, R3, R4, R> =
    SequenceMapParser4(parser1, parser2, parser3, parser4, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser4<*, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 5 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser5<R1, R2, R3, R4, R5, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  val callback: (R1, R2, R3, R4, R5) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = parser5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    return r5.success(callback(r1.value, r2.value, r3.value, r4.value, r5.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser5.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4, parser5)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
    if (parser5 == source) parser5 = target as Parser<R5>
  }

  override fun copy(): SequenceMapParser5<R1, R2, R3, R4, R5, R> =
    SequenceMapParser5(parser1, parser2, parser3, parser4, parser5, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser5<*, *, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 6 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser6<R1, R2, R3, R4, R5, R6, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  val callback: (R1, R2, R3, R4, R5, R6) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = parser5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = parser6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    return r6.success(callback(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser5.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser6.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4, parser5, parser6)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
    if (parser5 == source) parser5 = target as Parser<R5>
    if (parser6 == source) parser6 = target as Parser<R6>
  }

  override fun copy(): SequenceMapParser6<R1, R2, R3, R4, R5, R6, R> =
    SequenceMapParser6(parser1, parser2, parser3, parser4, parser5, parser6, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser6<*, *, *, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 7 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
  val callback: (R1, R2, R3, R4, R5, R6, R7) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = parser5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = parser6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = parser7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    return r7.success(callback(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser5.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser6.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser7.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4, parser5, parser6, parser7)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
    if (parser5 == source) parser5 = target as Parser<R5>
    if (parser6 == source) parser6 = target as Parser<R6>
    if (parser7 == source) parser7 = target as Parser<R7>
  }

  override fun copy(): SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, R> =
    SequenceMapParser7(parser1, parser2, parser3, parser4, parser5, parser6, parser7, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser7<*, *, *, *, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 8 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
  var parser8: Parser<R8>,
  val callback: (R1, R2, R3, R4, R5, R6, R7, R8) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = parser5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = parser6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = parser7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    val r8 = parser8.parseOn(r7)
    if (r8 is Output.Failure) return r8
    return r8.success(callback(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value, r8.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser5.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser6.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser7.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser8.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
    if (parser5 == source) parser5 = target as Parser<R5>
    if (parser6 == source) parser6 = target as Parser<R6>
    if (parser7 == source) parser7 = target as Parser<R7>
    if (parser8 == source) parser8 = target as Parser<R8>
  }

  override fun copy(): SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, R> =
    SequenceMapParser8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, callback, hasSideEffects)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser8<*, *, *, *, *, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}

/** A parser that runs 9 parsers in sequence and maps the results using [callback]. */
class SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, out R>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
  var parser8: Parser<R8>,
  var parser9: Parser<R9>,
  val callback: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R,
  val hasSideEffects: Boolean = false,
) : Parser<R> {
  override fun parseOn(input: Input): Output<R> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = parser5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = parser6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = parser7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    val r8 = parser8.parseOn(r7)
    if (r8 is Output.Failure) return r8
    val r9 = parser9.parseOn(r8)
    if (r9 is Output.Failure) return r9
    return r9.success(
      callback(
        r1.value,
        r2.value,
        r3.value,
        r4.value,
        r5.value,
        r6.value,
        r7.value,
        r8.value,
        r9.value,
      )
    )
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    if (hasSideEffects) return super.fastParseOn(buffer, position)
    var pos = parser1.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = parser2.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser3.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser4.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser5.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser6.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser7.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser8.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    pos = parser9.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return pos
  }

  override val children: List<Parser<*>>
    get() = listOf(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, parser9)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (parser1 == source) parser1 = target as Parser<R1>
    if (parser2 == source) parser2 = target as Parser<R2>
    if (parser3 == source) parser3 = target as Parser<R3>
    if (parser4 == source) parser4 = target as Parser<R4>
    if (parser5 == source) parser5 = target as Parser<R5>
    if (parser6 == source) parser6 = target as Parser<R6>
    if (parser7 == source) parser7 = target as Parser<R7>
    if (parser8 == source) parser8 = target as Parser<R8>
    if (parser9 == source) parser9 = target as Parser<R9>
  }

  override fun copy(): SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, R> =
    SequenceMapParser9(
      parser1,
      parser2,
      parser3,
      parser4,
      parser5,
      parser6,
      parser7,
      parser8,
      parser9,
      callback,
      hasSideEffects,
    )

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is SequenceMapParser9<*, *, *, *, *, *, *, *, *, *> &&
      callback == other.callback &&
      hasSideEffects == other.hasSideEffects

  override fun toString(): String = "${this::class.simpleName}"
}