package org.petitparser.core.expression

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.ChoiceParser
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.repeater.plusSeparated
import org.petitparser.core.parser.repeater.star

/**
 * Models a group of operators of the same precedence.
 */
class ExpressionGroup<T>(
  private val loopback: Parser<T>,
) {
  private val primitives = mutableListOf<Parser<T>>()
  private val wrappers = mutableListOf<Parser<T>>()
  private val prefixes = mutableListOf<Parser<ExpressionResultPrefix<T, *>>>()
  private val postfixes = mutableListOf<Parser<ExpressionResultPostfix<T, *>>>()
  private val rights = mutableListOf<Parser<ExpressionResultInfix<T, *>>>()
  private val lefts = mutableListOf<Parser<ExpressionResultInfix<T, *>>>()
  private var hasOptional = false
  private var optionalValue: T? = null

  /**
   * Defines a new primitive, literal, or value [parser] at this group level.
   */
  fun primitive(parser: Parser<T>) {
    primitives.add(parser)
  }

  /**
   * Defines a new wrapper using [left] and [right] parsers, typically used for parentheses.
   * Evaluates the [callback] with the parsed [left] delimiter, the value, and [right] delimiter.
   */
  fun <L, R> wrapper(
    left: Parser<L>,
    right: Parser<R>,
    callback: (L, T, R) -> T,
  ) {
    wrappers.add(seqMap(left, loopback, right, callback))
  }

  /**
   * Defines a new wrapper using [left] and [right] parsers, returning the parsed value.
   */
  fun wrapper(
    left: Parser<*>,
    right: Parser<*>,
  ) {
    wrapper(left, right) { _, value, _ -> value }
  }

  /**
   * Adds a prefix operator [parser].
   * Evaluates the [callback] with the parsed operator and value.
   */
  fun <O> prefix(
    parser: Parser<O>,
    callback: (O, T) -> T,
  ) {
    prefixes.add(parser.map { op -> ExpressionResultPrefix(op, callback) })
  }

  /**
   * Adds a prefix operator [parser].
   * Evaluates the [callback] with the parsed value.
   */
  fun prefix(
    parser: Parser<*>,
    callback: (T) -> T,
  ) {
    prefix(parser) { _, value -> callback(value) }
  }

  /**
   * Adds a postfix operator [parser].
   * Evaluates the [callback] with the parsed value and operator.
   */
  fun <O> postfix(
    parser: Parser<O>,
    callback: (T, O) -> T,
  ) {
    postfixes.add(parser.map { op -> ExpressionResultPostfix(op, callback) })
  }

  /**
   * Adds a postfix operator [parser].
   * Evaluates the [callback] with the parsed value.
   */
  fun postfix(
    parser: Parser<*>,
    callback: (T) -> T,
  ) {
    postfix(parser) { value, _ -> callback(value) }
  }

  /**
   * Adds a right-associative operator [parser].
   * Evaluates the [callback] with the parsed left term, operator, and right term.
   */
  fun <O> right(
    parser: Parser<O>,
    callback: (T, O, T) -> T,
  ) {
    rights.add(parser.map { op -> ExpressionResultInfix(op, callback) })
  }

  /**
   * Adds a right-associative operator [parser].
   * Evaluates the [callback] with the parsed left term and right term.
   */
  fun right(
    parser: Parser<*>,
    callback: (T, T) -> T,
  ) {
    right(parser) { left, _, right -> callback(left, right) }
  }

  /**
   * Adds a left-associative operator [parser].
   * Evaluates the [callback] with the parsed left term, operator, and right term.
   */
  fun <O> left(
    parser: Parser<O>,
    callback: (T, O, T) -> T,
  ) {
    lefts.add(parser.map { op -> ExpressionResultInfix(op, callback) })
  }

  /**
   * Adds a left-associative operator [parser].
   * Evaluates the [callback] with the parsed left term and right term.
   */
  fun left(
    parser: Parser<*>,
    callback: (T, T) -> T,
  ) {
    left(parser) { left, _, right -> callback(left, right) }
  }

  /**
   * Makes the group optional and instead returns the provided [value].
   */
  fun optional(value: T) {
    check(!hasOptional) { "At most one optional value expected" }
    optionalValue = value
    hasOptional = true
  }

  internal fun build(inner: Parser<T>?): Parser<T> {
    val base: Parser<T> = when {
      inner == null && primitives.isEmpty() -> throw IllegalStateException("At least one primitive parser expected")
      inner == null -> buildChoice(primitives)
      primitives.isEmpty() -> inner
      else -> buildChoice(primitives + inner)
    }
    return buildOptional(
      buildLeft(
        buildRight(
          buildPostfix(
            buildPrefix(
              buildWrapper(base),
            ),
          ),
        ),
      ),
    )
  }

  private fun buildWrapper(inner: Parser<T>): Parser<T> =
    if (wrappers.isEmpty()) inner else buildChoice(wrappers + inner)

  private fun buildPrefix(inner: Parser<T>): Parser<T> {
    if (prefixes.isEmpty()) return inner
    return seqMap(buildChoice(prefixes).star(), inner) { prefixList, value ->
      prefixList.asReversed().fold(value) { acc, result -> result(acc) }
    }
  }

  private fun buildPostfix(inner: Parser<T>): Parser<T> {
    if (postfixes.isEmpty()) return inner
    return seqMap(inner, buildChoice(postfixes).star()) { value, postfixList ->
      postfixList.fold(value) { acc, result -> result(acc) }
    }
  }

  private fun buildRight(inner: Parser<T>): Parser<T> {
    if (rights.isEmpty()) return inner
    return inner.plusSeparated(buildChoice(rights)).map { sequence ->
      sequence.foldRight { left, result, right -> result(left, right) }
    }
  }

  private fun buildLeft(inner: Parser<T>): Parser<T> {
    if (lefts.isEmpty()) return inner
    return inner.plusSeparated(buildChoice(lefts)).map { sequence ->
      sequence.foldLeft { left, result, right -> result(left, right) }
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun buildOptional(inner: Parser<T>): Parser<T> =
    if (hasOptional) inner.optional(optionalValue as T) else inner

  companion object {
    @Suppress("UNCHECKED_CAST")
    internal fun <R> buildChoice(parsers: List<Parser<R>>): Parser<R> =
      if (parsers.size == 1) parsers[0] else ChoiceParser(parsers)
  }
}
