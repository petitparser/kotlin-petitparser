package org.petitparser.core.parser.combinator

import org.petitparser.core.parser.Parser

/**
 * An abstract parser that delegates to a parser of type [T] and returns a result of type [R].
 */
abstract class DelegateParser<T, out R>(var delegate: Parser<T>) : Parser<R> {
  override val children: List<Parser<*>>
    get() = listOf(delegate)

  @Suppress("UNCHECKED_CAST")
  override fun replace(source: Parser<*>, target: Parser<*>) {
    super.replace(source, target)
    if (delegate == source) {
      delegate = target as Parser<T>
    }
  }
}
