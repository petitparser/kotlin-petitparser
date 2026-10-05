package org.petitparser.core.parser.action

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.DelegateParser
import org.petitparser.core.parser.consumer.whitespace

/**
 * Returns a parser that consumes input before and after the receiver, discards the excess input
 * and only returns the result of the receiver.
 */
fun <R> Parser<R>.trim(
  left: Parser<*> = whitespace(),
  right: Parser<*> = left,
): Parser<R> = TrimmingParser(this, left, right)

/**
 * A parser that silently consumes input of another parser around its delegate.
 */
class TrimmingParser<R>(
  delegate: Parser<R>,
  var left: Parser<*>,
  var right: Parser<*>,
) : DelegateParser<R, R>(delegate) {
  override val children: List<Parser<*>>
    get() = listOf(delegate, left, right)

  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (left == source) {
      left = target
    }
    if (right == source) {
      right = target
    }
  }

  override fun parseOn(input: Input): Output<R> {
    val buffer = input.buffer
    val before = trim(left, buffer, input.position)
    val context = if (before != input.position) Input.Impl(buffer, before) else input
    val result = delegate.parseOn(context)
    if (result is Output.Failure) return result
    val after = trim(right, buffer, result.position)
    return if (after == result.position) result else result.success(result.value, after)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    val result = delegate.fastParseOn(buffer, trim(left, buffer, position))
    return if (result < 0) -1 else trim(right, buffer, result)
  }

  private fun trim(parser: Parser<*>, buffer: String, startPosition: Int): Int {
    var position = startPosition
    while (true) {
      val result = parser.fastParseOn(buffer, position)
      check(result != position) { "$parser must always consume" }
      if (result < 0) {
        break
      }
      position = result
    }
    return position
  }

  override fun copy(): TrimmingParser<R> = TrimmingParser(delegate, left, right)
}