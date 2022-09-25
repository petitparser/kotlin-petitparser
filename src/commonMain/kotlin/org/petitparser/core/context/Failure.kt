package org.petitparser.core.context

class Failure<R>(
  override val buffer: String,
  override val position: Int,
  override val message: String,
) : Result<R> {
  override val value: R
    get() = throw ParseError(this)
}
