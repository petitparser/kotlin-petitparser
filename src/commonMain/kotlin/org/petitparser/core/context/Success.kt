package org.petitparser.core.context

class Success<R>(override val buffer: String, override val position: Int, override val value: R) :
  Result<R> {
  override val message: String
    get() = throw UnsupportedOperationException()

  override fun toString(): String = "Success[$position]: $value"
}