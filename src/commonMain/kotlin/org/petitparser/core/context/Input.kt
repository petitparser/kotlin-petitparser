package org.petitparser.core.context

interface Input {
  val buffer: String
  val position: Int

  fun <R> success(value: R, position: Int = this.position) =
    Output.Success(buffer, position, value)

  fun <R> failure(message: String, position: Int = this.position) =
    Output.Failure<R>(buffer, position, message)

  data class InputImpl(override val buffer: String, override val position: Int) : Input
}



