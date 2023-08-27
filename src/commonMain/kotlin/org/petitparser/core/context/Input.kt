package org.petitparser.core.context

interface Input {
  val buffer: String
  val position: Int

  data class Impl(override val buffer: String, override val position: Int) : Input
}
