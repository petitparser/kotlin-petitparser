package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.Parser

/**
 * An abstract parser that parses a list of child parsers in some way.
 */
abstract class ListParser<R, out S>(children: Iterable<Parser<R>>) : Parser<S> {
  constructor(vararg children: Parser<R>) : this(children.toList())

  protected val parsers: MutableList<Parser<R>> = children.toMutableList()

  override val children: List<Parser<*>>
    get() = parsers

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    for (i in parsers.indices) {
      if (parsers[i] == source) {
        parsers[i] = target as Parser<R>
      }
    }
  }

  override fun toString(): String = "${this::class.simpleName}"
}
