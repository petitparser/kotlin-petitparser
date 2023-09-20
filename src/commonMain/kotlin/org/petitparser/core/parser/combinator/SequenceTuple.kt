package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.utils.Tuple2
import org.petitparser.core.parser.utils.Tuple3
import org.petitparser.core.parser.utils.Tuple4
import org.petitparser.core.parser.utils.Tuple5
import org.petitparser.core.parser.utils.Tuple6
import org.petitparser.core.parser.utils.Tuple7
import org.petitparser.core.parser.utils.Tuple8
import org.petitparser.core.parser.utils.Tuple9

/** Sequence of two parsers [p1] and [p2] returning a [Tuple2]. */
fun <R1, R2> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
) = object : Parser<Tuple2<R1, R2>> {
  override fun parseOn(input: Input): Output<Tuple2<R1, R2>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    return r2.success(Tuple2(r1.value, r2.value))
  }
}

/** Sequence of three parsers [p1], ..., [p3] returning a [Tuple3]. */
fun <R1, R2, R3> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
) = object : Parser<Tuple3<R1, R2, R3>> {
  override fun parseOn(input: Input): Output<Tuple3<R1, R2, R3>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    return r3.success(Tuple3(r1.value, r2.value, r3.value))
  }
}

/** Sequence of four parsers [p1], ..., [p4] returning a [Tuple4]. */
fun <R1, R2, R3, R4> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
) = object : Parser<Tuple4<R1, R2, R3, R4>> {
  override fun parseOn(input: Input): Output<Tuple4<R1, R2, R3, R4>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    return r4.success(Tuple4(r1.value, r2.value, r3.value, r4.value))
  }
}

/** Sequence of five parsers [p1], ..., [p5] returning a [Tuple5]. */
fun <R1, R2, R3, R4, R5> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
) = object : Parser<Tuple5<R1, R2, R3, R4, R5>> {
  override fun parseOn(input: Input): Output<Tuple5<R1, R2, R3, R4, R5>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = p5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    return r5.success(Tuple5(r1.value, r2.value, r3.value, r4.value, r5.value))
  }
}

/** Sequence of six parsers [p1], ..., [p6] returning a [Tuple6]. */
fun <R1, R2, R3, R4, R5, R6> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
) = object : Parser<Tuple6<R1, R2, R3, R4, R5, R6>> {
  override fun parseOn(input: Input): Output<Tuple6<R1, R2, R3, R4, R5, R6>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = p5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = p6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    return r6.success(Tuple6(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value))
  }
}

/** Sequence of seven parsers [p1], ..., [p7] returning a [Tuple7]. */
fun <R1, R2, R3, R4, R5, R6, R7> seq(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  p7: Parser<R7>,
) = object : Parser<Tuple7<R1, R2, R3, R4, R5, R6, R7>> {
  override fun parseOn(input: Input): Output<Tuple7<R1, R2, R3, R4, R5, R6, R7>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = p5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = p6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = p7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    return r7.success(Tuple7(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value))
  }
}

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
) = object : Parser<Tuple8<R1, R2, R3, R4, R5, R6, R7, R8>> {
  override fun parseOn(input: Input): Output<Tuple8<R1, R2, R3, R4, R5, R6, R7, R8>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = p5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = p6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = p7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    val r8 = p8.parseOn(r7)
    if (r8 is Output.Failure) return r8
    return r8.success(Tuple8(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value, r8.value))
  }
}

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
) = object : Parser<Tuple9<R1, R2, R3, R4, R5, R6, R7, R8, R9>> {
  override fun parseOn(input: Input): Output<Tuple9<R1, R2, R3, R4, R5, R6, R7, R8, R9>> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    val r5 = p5.parseOn(r4)
    if (r5 is Output.Failure) return r5
    val r6 = p6.parseOn(r5)
    if (r6 is Output.Failure) return r6
    val r7 = p7.parseOn(r6)
    if (r7 is Output.Failure) return r7
    val r8 = p8.parseOn(r7)
    if (r8 is Output.Failure) return r8
    val r9 = p9.parseOn(r8)
    if (r9 is Output.Failure) return r9
    return r9.success(Tuple9(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value, r8.value, r9.value))
  }
}