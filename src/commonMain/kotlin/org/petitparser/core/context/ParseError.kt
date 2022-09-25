package org.petitparser.core.context

class ParseError(val failure: Failure<*>) : RuntimeException(failure.message) {
  val position: Int get() = failure.position
}