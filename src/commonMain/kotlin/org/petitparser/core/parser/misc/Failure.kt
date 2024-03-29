package org.petitparser.core.parser.misc

import org.petitparser.core.context.failure
import org.petitparser.core.parser.Parser

/** Returns a parser that consumes nothing and fails with a [message]. */
fun <R> failure(message: String = "unable to read") = Parser<R> { input -> input.failure(message) }