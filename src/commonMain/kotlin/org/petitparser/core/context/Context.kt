package org.petitparser.core.context

interface Context {
  val buffer: String
  val position: Int

  fun <R> success(value: R, position: Int = this.position) =
    Success(buffer, position, value)

  fun <R> failure(message: String, position: Int = this.position) =
    Failure<R>(buffer, position, message)
}

class ContextImpl(override val buffer: String, override val position: Int) : Context {
  override fun toString(): String = "Context[$position]"
}