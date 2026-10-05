package org.petitparser.core.definition

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser

/**
 * A parser that holds a reference to a production function with arguments.
 * Resolves to the parser produced by the function during [resolve].
 */
class ReferenceParser<R>(
  val function: Any,
  val arguments: List<Any?>,
  private val callback: () -> Parser<R>,
) : Parser<R>, ResolvableParser<R> {
  override fun resolve(): Parser<R> = callback()

  override fun parseOn(input: Input): Output<R> =
    throw UnsupportedOperationException("Unsupported operation on parser reference")

  override fun fastParseOn(buffer: String, position: Int): Int =
    throw UnsupportedOperationException("Unsupported operation on parser reference")

  override fun copy(): ReferenceParser<R> =
    ReferenceParser(function, arguments, callback)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ReferenceParser<*>) return false
    if (function != other.function) return false
    if (arguments.size != other.arguments.size) return false
    for (i in arguments.indices) {
      val a = arguments[i]
      val b = other.arguments[i]
      if (a is Parser<*> && a !is ReferenceParser<*> && b is Parser<*> && b !is ReferenceParser<*>) {
        if (!a.isEqualTo(b)) return false
      } else {
        if (a != b) return false
      }
    }
    return true
  }

  override fun hashCode(): Int = function.hashCode()

  override fun hasEqualProperties(other: Parser<*>): Boolean = this == other

  override fun toString(): String = "ReferenceParser($function, $arguments)"
}

/** Creates a [Parser] from a function reference without arguments. */
fun <R> ref(function: () -> Parser<R>): Parser<R> =
  ReferenceParser(function, emptyList(), function)

/** Creates a [Parser] from a function reference with 1 argument. */
fun <R, A1> ref(function: (A1) -> Parser<R>, a1: A1): Parser<R> =
  ReferenceParser(function, listOf(a1)) { function(a1) }

/** Creates a [Parser] from a function reference with 2 arguments. */
fun <R, A1, A2> ref(function: (A1, A2) -> Parser<R>, a1: A1, a2: A2): Parser<R> =
  ReferenceParser(function, listOf(a1, a2)) { function(a1, a2) }

/** Creates a [Parser] from a function reference with 3 arguments. */
fun <R, A1, A2, A3> ref(function: (A1, A2, A3) -> Parser<R>, a1: A1, a2: A2, a3: A3): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3)) { function(a1, a2, a3) }

/** Creates a [Parser] from a function reference with 4 arguments. */
fun <R, A1, A2, A3, A4> ref(function: (A1, A2, A3, A4) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4)) { function(a1, a2, a3, a4) }

/** Creates a [Parser] from a function reference with 5 arguments. */
fun <R, A1, A2, A3, A4, A5> ref(function: (A1, A2, A3, A4, A5) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4, a5)) { function(a1, a2, a3, a4, a5) }

/** Creates a [Parser] from a function reference with 6 arguments. */
fun <R, A1, A2, A3, A4, A5, A6> ref(function: (A1, A2, A3, A4, A5, A6) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4, a5, a6)) { function(a1, a2, a3, a4, a5, a6) }

/** Creates a [Parser] from a function reference with 7 arguments. */
fun <R, A1, A2, A3, A4, A5, A6, A7> ref(function: (A1, A2, A3, A4, A5, A6, A7) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4, a5, a6, a7)) { function(a1, a2, a3, a4, a5, a6, a7) }

/** Creates a [Parser] from a function reference with 8 arguments. */
fun <R, A1, A2, A3, A4, A5, A6, A7, A8> ref(function: (A1, A2, A3, A4, A5, A6, A7, A8) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7, a8: A8): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4, a5, a6, a7, a8)) { function(a1, a2, a3, a4, a5, a6, a7, a8) }

/** Creates a [Parser] from a function reference with 9 arguments. */
fun <R, A1, A2, A3, A4, A5, A6, A7, A8, A9> ref(function: (A1, A2, A3, A4, A5, A6, A7, A8, A9) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7, a8: A8, a9: A9): Parser<R> =
  ReferenceParser(function, listOf(a1, a2, a3, a4, a5, a6, a7, a8, a9)) { function(a1, a2, a3, a4, a5, a6, a7, a8, a9) }

// Canonical Dart arity aliases
fun <R> ref0(function: () -> Parser<R>): Parser<R> = ref(function)
fun <R, A1> ref1(function: (A1) -> Parser<R>, a1: A1): Parser<R> = ref(function, a1)
fun <R, A1, A2> ref2(function: (A1, A2) -> Parser<R>, a1: A1, a2: A2): Parser<R> = ref(function, a1, a2)
fun <R, A1, A2, A3> ref3(function: (A1, A2, A3) -> Parser<R>, a1: A1, a2: A2, a3: A3): Parser<R> = ref(function, a1, a2, a3)
fun <R, A1, A2, A3, A4> ref4(function: (A1, A2, A3, A4) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4): Parser<R> = ref(function, a1, a2, a3, a4)
fun <R, A1, A2, A3, A4, A5> ref5(function: (A1, A2, A3, A4, A5) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5): Parser<R> = ref(function, a1, a2, a3, a4, a5)
fun <R, A1, A2, A3, A4, A5, A6> ref6(function: (A1, A2, A3, A4, A5, A6) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6): Parser<R> = ref(function, a1, a2, a3, a4, a5, a6)
fun <R, A1, A2, A3, A4, A5, A6, A7> ref7(function: (A1, A2, A3, A4, A5, A6, A7) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7): Parser<R> = ref(function, a1, a2, a3, a4, a5, a6, a7)
fun <R, A1, A2, A3, A4, A5, A6, A7, A8> ref8(function: (A1, A2, A3, A4, A5, A6, A7, A8) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7, a8: A8): Parser<R> = ref(function, a1, a2, a3, a4, a5, a6, a7, a8)
fun <R, A1, A2, A3, A4, A5, A6, A7, A8, A9> ref9(function: (A1, A2, A3, A4, A5, A6, A7, A8, A9) -> Parser<R>, a1: A1, a2: A2, a3: A3, a4: A4, a5: A5, a6: A6, a7: A7, a8: A8, a9: A9): Parser<R> = ref(function, a1, a2, a3, a4, a5, a6, a7, a8, a9)
