package org.petitparser.core.context

class ParseError(val failure: Output.Failure) : RuntimeException(failure.message)