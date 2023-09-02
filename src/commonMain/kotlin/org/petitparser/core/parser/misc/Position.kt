package org.petitparser.core.parser.misc

import org.petitparser.core.context.success
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and produces the current position in the input. */
fun position() = Parser { input -> input.success(input.position) }