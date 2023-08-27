package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output

interface Parser<out R> {
  fun parseOn(input: Input): Output<R>
}


