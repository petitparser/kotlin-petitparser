package org.petitparser.core.parser

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output

/** Functional interface of all parsers. */
fun interface Parser<out R> {
  /** Parses the given [input]. */
  fun parseOn(input: Input): Output<R>

  /**
   * Parses [buffer] starting at [position] without allocating an [Output].
   * Returns the ending position on success, or `-1` on failure.
   */
  fun fastParseOn(buffer: String, position: Int): Int {
    val result = parseOn(Input.Impl(buffer, position))
    return if (result is Output.Failure) -1 else result.position
  }

  /** Creates a shallow copy of this parser. */
  fun copy(): Parser<R> = this

  /** The list of directly referenced child parsers. */
  val children: List<Parser<*>>
    get() = emptyList()

  /** Replaces [source] with [target] in this parser's children. */
  fun replace(source: Parser<*>, target: Parser<*>) {}

  /** Tests if this parser is structurally equal to [other]. */
  fun isEqualTo(other: Any?, seen: MutableSet<Parser<*>> = mutableSetOf()): Boolean {
    if (this === other) return true
    if (other !is Parser<*>) return false
    if (this::class != other::class) return false
    if (!hasEqualProperties(other)) return false
    return !seen.add(this) || hasEqualChildren(other, seen)
  }

  /** Compares the properties of this parser with [other]. */
  fun hasEqualProperties(other: Parser<*>): Boolean = true

  /** Compares the children of this parser with [other]. */
  fun hasEqualChildren(other: Parser<*>, seen: MutableSet<Parser<*>>): Boolean {
    val thisChildren = children
    val otherChildren = other.children
    if (thisChildren.size != otherChildren.size) return false
    for (i in thisChildren.indices) {
      if (!thisChildren[i].isEqualTo(otherChildren[i], seen)) return false
    }
    return true
  }
}
