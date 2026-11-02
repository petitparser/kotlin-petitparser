package org.petitparser.core.definition

import org.petitparser.core.parser.Parser

/**
 * Interface of a parser that can be resolved to another one.
 */
interface ResolvableParser<out R> : Parser<R> {
  /** Resolves this parser with another one of the same type. */
  fun resolve(): Parser<R>
}
