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

  data class Failure<out R>(
    override val buffer: String,
    override val position: Int,
    override val message: String,
  ) : Output<R> {
    override val value: R
      get() = throw ParseError(this)
  }
}

