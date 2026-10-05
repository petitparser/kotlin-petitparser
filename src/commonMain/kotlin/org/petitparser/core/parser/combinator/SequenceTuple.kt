package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.utils.SequentialParser
import org.petitparser.core.parser.utils.Tuple2
import org.petitparser.core.parser.utils.Tuple3
import org.petitparser.core.parser.utils.Tuple4
import org.petitparser.core.parser.utils.Tuple5
import org.petitparser.core.parser.utils.Tuple6
import org.petitparser.core.parser.utils.Tuple7
import org.petitparser.core.parser.utils.Tuple8
import org.petitparser.core.parser.utils.Tuple9

/** Sequences the receiver and [other], returning a [SequenceParser2]. */
fun <R1, R2> Parser<R1>.then(other: Parser<R2>): SequenceParser2<R1, R2> =
  SequenceParser2(this, other)

/** Sequence of two parsers [p1] and [p2] returning a [Tuple2]. */
fun <R1, R2> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
): SequenceParser2<R1, R2> = SequenceParser2(p1, p2)

/** Sequence of three parsers [p1], ..., [p3] returning a [Tuple3]. */
fun <R1, R2, R3> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
): SequenceParser3<R1, R2, R3> = SequenceParser3(p1, p2, p3)

/** Sequence of four parsers [p1], ..., [p4] returning a [Tuple4]. */
fun <R1, R2, R3, R4> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
): SequenceParser4<R1, R2, R3, R4> = SequenceParser4(p1, p2, p3, p4)

/** Sequence of five parsers [p1], ..., [p5] returning a [Tuple5]. */
fun <R1, R2, R3, R4, R5> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
): SequenceParser5<R1, R2, R3, R4, R5> = SequenceParser5(p1, p2, p3, p4, p5)

/** Sequence of six parsers [p1], ..., [p6] returning a [Tuple6]. */
fun <R1, R2, R3, R4, R5, R6> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
): SequenceParser6<R1, R2, R3, R4, R5, R6> = SequenceParser6(p1, p2, p3, p4, p5, p6)

/** Sequence of seven parsers [p1], ..., [p7] returning a [Tuple7]. */
fun <R1, R2, R3, R4, R5, R6, R7> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
): SequenceParser7<R1, R2, R3, R4, R5, R6, R7> = SequenceParser7(p1, p2, p3, p4, p5, p6, p7)

/** Sequence of eight parsers [p1], ..., [p8] returning a [Tuple8]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
): SequenceParser8<R1, R2, R3, R4, R5, R6, R7, R8> = SequenceParser8(p1, p2, p3, p4, p5, p6, p7, p8)

/** Sequence of nine parsers [p1], ..., [p9] returning a [Tuple9]. */
fun <R1, R2, R3, R4, R5, R6, R7, R8, R9> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
  p8: Parser<R8>,
  p9: Parser<R9>,
): SequenceParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9> =
  SequenceParser9(p1, p2, p3, p4, p5, p6, p7, p8, p9)

/** A parser that consumes a sequence of 2 parsers and returns a [Tuple2]. */
class SequenceParser2<R1, R2>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
) : Parser<Tuple2<R1, R2>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple2<R1, R2>> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    return r2.success(Tuple2(r1.value, r2.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser2<R1, R2> = SequenceParser2(parser1, parser2)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R3> then(other: Parser<R3>): SequenceParser3<R1, R2, R3> =
    SequenceParser3(parser1, parser2, other)

  fun <R> map2(callback: (R1, R2) -> R): SequenceMapParser2<R1, R2, R> =
    SequenceMapParser2(parser1, parser2, callback)

  fun <R> map2(hasSideEffects: Boolean, callback: (R1, R2) -> R): SequenceMapParser2<R1, R2, R> =
    SequenceMapParser2(parser1, parser2, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 3 parsers and returns a [Tuple3]. */
class SequenceParser3<R1, R2, R3>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
) : Parser<Tuple3<R1, R2, R3>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple3<R1, R2, R3>> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    return r3.success(Tuple3(r1.value, r2.value, r3.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser3<R1, R2, R3> = SequenceParser3(parser1, parser2, parser3)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R4> then(other: Parser<R4>): SequenceParser4<R1, R2, R3, R4> =
    SequenceParser4(parser1, parser2, parser3, other)

  fun <R> map3(callback: (R1, R2, R3) -> R): SequenceMapParser3<R1, R2, R3, R> =
    SequenceMapParser3(parser1, parser2, parser3, callback)

  fun <R> map3(hasSideEffects: Boolean, callback: (R1, R2, R3) -> R): SequenceMapParser3<R1, R2, R3, R> =
    SequenceMapParser3(parser1, parser2, parser3, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 4 parsers and returns a [Tuple4]. */
class SequenceParser4<R1, R2, R3, R4>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
) : Parser<Tuple4<R1, R2, R3, R4>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple4<R1, R2, R3, R4>> {
    val r1 = parser1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = parser2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = parser3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = parser4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    return r4.success(Tuple4(r1.value, r2.value, r3.value, r4.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser4<R1, R2, R3, R4> =
    SequenceParser4(parser1, parser2, parser3, parser4)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R5> then(other: Parser<R5>): SequenceParser5<R1, R2, R3, R4, R5> =
    SequenceParser5(parser1, parser2, parser3, parser4, other)

  fun <R> map4(callback: (R1, R2, R3, R4) -> R): SequenceMapParser4<R1, R2, R3, R4, R> =
    SequenceMapParser4(parser1, parser2, parser3, parser4, callback)

  fun <R> map4(hasSideEffects: Boolean, callback: (R1, R2, R3, R4) -> R): SequenceMapParser4<R1, R2, R3, R4, R> =
    SequenceMapParser4(parser1, parser2, parser3, parser4, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 5 parsers and returns a [Tuple5]. */
class SequenceParser5<R1, R2, R3, R4, R5>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
) : Parser<Tuple5<R1, R2, R3, R4, R5>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple5<R1, R2, R3, R4, R5>> {
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
    return r5.success(Tuple5(r1.value, r2.value, r3.value, r4.value, r5.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser5<R1, R2, R3, R4, R5> =
    SequenceParser5(parser1, parser2, parser3, parser4, parser5)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R6> then(other: Parser<R6>): SequenceParser6<R1, R2, R3, R4, R5, R6> =
    SequenceParser6(parser1, parser2, parser3, parser4, parser5, other)

  fun <R> map5(callback: (R1, R2, R3, R4, R5) -> R): SequenceMapParser5<R1, R2, R3, R4, R5, R> =
    SequenceMapParser5(parser1, parser2, parser3, parser4, parser5, callback)

  fun <R> map5(hasSideEffects: Boolean, callback: (R1, R2, R3, R4, R5) -> R): SequenceMapParser5<R1, R2, R3, R4, R5, R> =
    SequenceMapParser5(parser1, parser2, parser3, parser4, parser5, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 6 parsers and returns a [Tuple6]. */
class SequenceParser6<R1, R2, R3, R4, R5, R6>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
) : Parser<Tuple6<R1, R2, R3, R4, R5, R6>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple6<R1, R2, R3, R4, R5, R6>> {
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
    return r6.success(Tuple6(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser6<R1, R2, R3, R4, R5, R6> =
    SequenceParser6(parser1, parser2, parser3, parser4, parser5, parser6)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R7> then(other: Parser<R7>): SequenceParser7<R1, R2, R3, R4, R5, R6, R7> =
    SequenceParser7(parser1, parser2, parser3, parser4, parser5, parser6, other)

  fun <R> map6(callback: (R1, R2, R3, R4, R5, R6) -> R): SequenceMapParser6<R1, R2, R3, R4, R5, R6, R> =
    SequenceMapParser6(parser1, parser2, parser3, parser4, parser5, parser6, callback)

  fun <R> map6(hasSideEffects: Boolean, callback: (R1, R2, R3, R4, R5, R6) -> R): SequenceMapParser6<R1, R2, R3, R4, R5, R6, R> =
    SequenceMapParser6(parser1, parser2, parser3, parser4, parser5, parser6, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 7 parsers and returns a [Tuple7]. */
class SequenceParser7<R1, R2, R3, R4, R5, R6, R7>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
) : Parser<Tuple7<R1, R2, R3, R4, R5, R6, R7>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple7<R1, R2, R3, R4, R5, R6, R7>> {
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
    return r7.success(Tuple7(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value))
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser7<R1, R2, R3, R4, R5, R6, R7> =
    SequenceParser7(parser1, parser2, parser3, parser4, parser5, parser6, parser7)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R8> then(other: Parser<R8>): SequenceParser8<R1, R2, R3, R4, R5, R6, R7, R8> =
    SequenceParser8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, other)

  fun <R> map7(callback: (R1, R2, R3, R4, R5, R6, R7) -> R): SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, R> =
    SequenceMapParser7(parser1, parser2, parser3, parser4, parser5, parser6, parser7, callback)

  fun <R> map7(hasSideEffects: Boolean, callback: (R1, R2, R3, R4, R5, R6, R7) -> R): SequenceMapParser7<R1, R2, R3, R4, R5, R6, R7, R> =
    SequenceMapParser7(parser1, parser2, parser3, parser4, parser5, parser6, parser7, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 8 parsers and returns a [Tuple8]. */
class SequenceParser8<R1, R2, R3, R4, R5, R6, R7, R8>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
  var parser8: Parser<R8>,
) : Parser<Tuple8<R1, R2, R3, R4, R5, R6, R7, R8>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple8<R1, R2, R3, R4, R5, R6, R7, R8>> {
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
    return r8.success(
      Tuple8(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value, r8.value)
    )
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
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

  override fun copy(): SequenceParser8<R1, R2, R3, R4, R5, R6, R7, R8> =
    SequenceParser8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8)

  override fun toString(): String = "${this::class.simpleName}"

  fun <R9> then(other: Parser<R9>): SequenceParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9> =
    SequenceParser9(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, other)

  fun <R> map8(callback: (R1, R2, R3, R4, R5, R6, R7, R8) -> R): SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, R> =
    SequenceMapParser8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, callback)

  fun <R> map8(hasSideEffects: Boolean, callback: (R1, R2, R3, R4, R5, R6, R7, R8) -> R): SequenceMapParser8<R1, R2, R3, R4, R5, R6, R7, R8, R> =
    SequenceMapParser8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, callback, hasSideEffects)
}

/** A parser that consumes a sequence of 9 parsers and returns a [Tuple9]. */
class SequenceParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9>(
  var parser1: Parser<R1>,
  var parser2: Parser<R2>,
  var parser3: Parser<R3>,
  var parser4: Parser<R4>,
  var parser5: Parser<R5>,
  var parser6: Parser<R6>,
  var parser7: Parser<R7>,
  var parser8: Parser<R8>,
  var parser9: Parser<R9>,
) : Parser<Tuple9<R1, R2, R3, R4, R5, R6, R7, R8, R9>>, SequentialParser {
  override fun parseOn(input: Input): Output<Tuple9<R1, R2, R3, R4, R5, R6, R7, R8, R9>> {
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
      Tuple9(
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

  override fun copy(): SequenceParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9> =
    SequenceParser9(
      parser1,
      parser2,
      parser3,
      parser4,
      parser5,
      parser6,
      parser7,
      parser8,
      parser9,
    )

  override fun toString(): String = "${this::class.simpleName}"

  fun <R> map9(callback: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R): SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, R> =
    SequenceMapParser9(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, parser9, callback)

  fun <R> map9(hasSideEffects: Boolean, callback: (R1, R2, R3, R4, R5, R6, R7, R8, R9) -> R): SequenceMapParser9<R1, R2, R3, R4, R5, R6, R7, R8, R9, R> =
    SequenceMapParser9(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8, parser9, callback, hasSideEffects)
}