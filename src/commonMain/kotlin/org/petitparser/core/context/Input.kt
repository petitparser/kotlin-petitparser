package org.petitparser.core.context

/** Input interface of the parser function. */
interface Input {
  /** The input buffer being read. */
  val buffer: String

  /** The position in the input buffer being read. */
  val position: Int

  /** Actual implementation of the [Input] interface. */
  data class Impl(override val buffer: String, override val position: Int) : Input
}
