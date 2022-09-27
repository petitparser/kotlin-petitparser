package org.petitparser.core.context

class ParseError(val failure: Output.Failure<Any?>) : RuntimeException(failure.message)