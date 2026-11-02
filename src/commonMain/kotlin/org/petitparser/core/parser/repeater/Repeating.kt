package org.petitparser.core.parser.repeater

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser

/**
 * An abstract parser that repeatedly parses between [min] and [max] instances of its delegate.
 */
abstract class RepeatingParser<T, out R>(
  delegate: Parser<T>,
  val min: Int,
  val max: Int = min,
) : DelegateParser<T, R>(delegate) {
  init {
    require(min >= 0) { "min must be at least 0, but got $min" }
    require(min <= max) { "max must be at least $min, but got $max" }
  }

  override fun toString(): String =
    "${super.toString()}[$min..${if (max == Int.MAX_VALUE) "*" else max}]"

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) &&
      other is RepeatingParser<*, *> &&
      min == other.min &&
      max == other.max
}

/**
 * An abstract parser that repeatedly parses between [min] and [max] instances of its delegate
 * and requires the input to be completed with a specified parser [limit].
 */
abstract class LimitedRepeatingParser<R>(
  delegate: Parser<R>,
  var limit: Parser<*>,
  min: Int,
  max: Int = min,
) : RepeatingParser<R, List<R>>(delegate, min, max) {
  override val children: List<Parser<*>>
    get() = listOf(delegate, limit)

  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (limit == source) {
      limit = target
    }
  }

  override fun hasEqualProperties(other: Parser<*>): Boolean =
    super.hasEqualProperties(other) && other is LimitedRepeatingParser<*>
}
