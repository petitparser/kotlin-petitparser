package org.petitparser.core.parser.combinator

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.misc.success

/**
 * Returns a parser that consumes input [before] and [after] the receiver, but discards the parse
 * results of [before] and [after] and only returns the result of the receiver.
 */
fun <R> Parser<R>.skip(before: Parser<*>? = null, after: Parser<*>? = null): SkipParser<R> =
  SkipParser(this, before ?: success(), after ?: success())

/** Returns a parser that parses the receiver then [delimiter], returning the receiver's result. */
fun <R> Parser<R>.followedBy(delimiter: Parser<*>): SkipParser<R> =
  skip(after = delimiter)

/** Returns a parser that parses [delimiter] then the receiver, returning the receiver's result. */
fun <R> Parser<R>.precededBy(delimiter: Parser<*>): SkipParser<R> =
  skip(before = delimiter)

/** Returns a parser that parses [left], the receiver, and [right], returning the receiver's result. */
fun <R> Parser<R>.surroundedBy(left: Parser<*>, right: Parser<*> = left): SkipParser<R> =
  skip(before = left, after = right)

/**
 * A parser that silently consumes input of another parser before and after
 * its [delegate].
 */
class SkipParser<R>(
  delegate: Parser<R>,
  var before: Parser<*> = success(),
  var after: Parser<*> = success(),
) : DelegateParser<R, R>(delegate) {
  override fun parseOn(input: Input): Output<R> {
    val beforeContext = before.parseOn(input)
    if (beforeContext is Output.Failure) return beforeContext
    val resultContext = delegate.parseOn(beforeContext)
    if (resultContext is Output.Failure) return resultContext
    val afterContext = after.parseOn(resultContext)
    if (afterContext is Output.Failure) return afterContext
    return afterContext.success(resultContext.value)
  }

  override fun fastParseOn(buffer: String, position: Int): Int {
    var pos = before.fastParseOn(buffer, position)
    if (pos < 0) return -1
    pos = delegate.fastParseOn(buffer, pos)
    if (pos < 0) return -1
    return after.fastParseOn(buffer, pos)
  }

  override val children: List<Parser<*>>
    get() = listOf(before, delegate, after)

  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (before == source) before = target
    if (after == source) after = target
  }

  override fun copy(): SkipParser<R> = SkipParser(delegate, before, after)
}