package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.failure
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

fun interface FailureFactory<in T> {
  operator fun invoke(input: Input, success: Output.Success<T>): Output.Failure
}

internal data class MessageFailureFactory(val message: String) : FailureFactory<Any?> {
  override fun invoke(input: Input, success: Output.Success<Any?>): Output.Failure =
    input.failure(message)
}

internal object DefaultFailureFactory : FailureFactory<Any?> {
  override fun invoke(input: Input, success: Output.Success<Any?>): Output.Failure =
    input.failure("unexpected '${success.value}'")
}

@Suppress("UNCHECKED_CAST")
fun <T> defaultFailureFactory(message: String?): FailureFactory<T> =
  if (message == null) {
    DefaultFailureFactory as FailureFactory<T>
  } else {
    MessageFailureFactory(message) as FailureFactory<T>
  }

/**
 * Returns a parser that evaluates the [predicate] on the successful parse result.
 * If the predicate returns `true`, the parser proceeds with the result, otherwise a parse failure
 * is created with a default message.
 */
fun <T> Parser<T>.filter(
  predicate: (T) -> Boolean,
): Parser<T> = FilterParser(this, predicate, defaultFailureFactory(null))

/**
 * Returns a parser that evaluates the [predicate] on the successful parse result.
 * If the predicate returns `true`, the parser proceeds with the result, otherwise a parse failure
 * is created with [message].
 */
fun <T> Parser<T>.filter(
  predicate: (T) -> Boolean,
  message: String?,
): Parser<T> = FilterParser(this, predicate, defaultFailureFactory(message))

/**
 * Returns a parser that evaluates the [predicate] on the successful parse result.
 * If the predicate returns `true`, the parser proceeds with the result, otherwise a parse failure
 * is created with the message computed by [message].
 */
fun <T> Parser<T>.filter(
  predicate: (T) -> Boolean,
  message: (T) -> String,
): Parser<T> = FilterParser(this, predicate) { input, success ->
  input.failure(message(success.value))
}

/**
 * Returns a parser that evaluates the [predicate] on the successful parse result.
 * If the predicate returns `true`, the parser proceeds with the result, otherwise a parse failure
 * is created using [factory].
 */
fun <T> Parser<T>.filter(
  predicate: (T) -> Boolean,
  factory: FailureFactory<T>,
): Parser<T> = FilterParser(this, predicate, factory)

/**
 * Returns a parser that evaluates the [predicate] on the successful parse result with custom [message].
 */
fun <T> Parser<T>.filter(
  message: String,
  predicate: (T) -> Boolean,
): Parser<T> = FilterParser(this, predicate, defaultFailureFactory(message))

/**
 * A parser that evaluates a predicate on the successful result of its delegate.
 */
class FilterParser<T>(
  delegate: Parser<T>,
  val predicate: (T) -> Boolean,
  val factory: FailureFactory<T> = defaultFailureFactory(null),
) : DelegateParser<T, T>(delegate) {
  val failureFactory: FailureFactory<T>
    get() = factory

  override fun parseOn(input: Input): Output<T> {
    val result = delegate.parseOn(input)
    if (result is Output.Success<T> && !predicate(result.value)) {
      return factory(input, result)
    }
    return result
  }

  override fun copy(): FilterParser<T> = FilterParser(delegate, predicate, factory)

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is FilterParser<*> &&
      predicate == other.predicate &&
      factory == other.factory
}

typealias WhereParser<T> = FilterParser<T>
