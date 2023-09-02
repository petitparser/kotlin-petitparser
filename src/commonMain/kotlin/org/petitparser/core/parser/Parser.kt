package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output

/** Functional interface of all parsers. */
fun interface Parser<out R> {
  fun parseOn(input: Input): Output<R>
}


