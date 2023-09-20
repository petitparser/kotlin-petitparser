package org.petitparser.core.context

/** A parse result, that is either a [Success] or a [Failure]. */
sealed interface Output<out R> : Input {
  /** The successfully parsed value of type [R] in case of [Success]. */
  val value: R

  /** The error message in case of [Failure]. */
  val message: String

  /** A successful parse result. */
  data class Success<out R>(
    override val buffer: String,
    override val position: Int,
    override val value: R,
  ) : Output<R> {
    override val message: String
      get() = throw UnsupportedOperationException()
  }

  /** A failing parse result. */
  data class Failure(
    override val buffer: String,
    override val position: Int,
    override val message: String,
  ) : Output<Nothing> {
    override val value: Nothing
      get() = throw ParseError(this)
  }
}

/** Constructs a parse [Output.Success] from the current [Input]. */
inline fun <R> Input.success(value: R, position: Int = this.position) = Output.Success(buffer, position, value)

/** Constructs a parse [Output.Failure] from the current [Input]. */
inline fun Input.failure(message: String, position: Int = this.position) = Output.Failure(buffer, position, message)
