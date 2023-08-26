package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/** Sequence of two parsers [p1] and [p2] with a strongly typed mapping function [block]. */
fun <R1, R2, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  block: (R1, R2) -> R,
) = object : Parser<R> {
  override val children = listOf(p1, p2)
  override fun parseOn(input: Input): Output<R> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    return r2.success(block(r1.value, r2.value))
  }
}

/** Sequence of tree parsers [p1], ..., [p3] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  block: (R1, R2, R3) -> R,
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3)
  override fun parseOn(input: Input): Output<R> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    return r3.success(block(r1.value, r2.value, r3.value))
  }
}

/** Sequence of four parsers [p1], ..., [p4] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  block: (R1, R2, R3, R4) -> R,
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3, p4)
  override fun parseOn(input: Input): Output<R> {
    val r1 = p1.parseOn(input)
    if (r1 is Output.Failure) return r1
    val r2 = p2.parseOn(r1)
    if (r2 is Output.Failure) return r2
    val r3 = p3.parseOn(r2)
    if (r3 is Output.Failure) return r3
    val r4 = p4.parseOn(r3)
    if (r4 is Output.Failure) return r4
    return r4.success(block(r1.value, r2.value, r3.value, r4.value))
  }
}

/** Sequence of five parsers [p1], ..., [p5] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  block: (R1, R2, R3, R4, R5) -> R,
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3, p4, p5)
  override fun parseOn(input: Input): Output<R> {
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
    return r5.success(block(r1.value, r2.value, r3.value, r4.value, r5.value))
  }
}

/** Sequence of six parsers [p1], ..., [p6] with a strongly typed mapping function [block]. */
fun <R1, R2, R3, R4, R5, R6, R> seqMap(
  p1: Parser<R1>,
  p2: Parser<R2>,
  p3: Parser<R3>,
  p4: Parser<R4>,
  p5: Parser<R5>,
  p6: Parser<R6>,
  block: (R1, R2, R3, R4, R5, R6) -> R,
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3, p4, p5, p6)
  override fun parseOn(input: Input): Output<R> {
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
    return r6.success(block(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value))
  }
}

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
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3, p4, p5, p6, p7)
  override fun parseOn(input: Input): Output<R> {
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
    return r7.success(block(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value))
  }
}

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
) = object : Parser<R> {
  override val children = listOf(p1, p2, p3, p4, p5, p6, p7, p8)
  override fun parseOn(input: Input): Output<R> {
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
    return r8.success(
      block(r1.value, r2.value, r3.value, r4.value, r5.value, r6.value, r7.value, r8.value)
    )
  }
}