package org.petitparser.core.context

sealed interface Output<out R> : Input {
  val value: R
  val message: String

  data class Success<out R>(
    override val buffer: String,
    override val position: Int,
    override val value: R,
  ) : Output<R> {
    override val message: String
      get() = throw UnsupportedOperationException()
  }

  data class Failure(
    override val buffer: String,
    override val position: Int,
    override val message: String,
  ) : Output<Nothing> {
    override val value: Nothing
      get() = throw ParseError(this)
  }
}

inline fun <R> Input.success(value: R, position: Int = this.position) = Output.Success(buffer, position, value)

inline fun Input.failure(message: String, position: Int = this.position) = Output.Failure(buffer, position, message)

