package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser

/**
 * Returns a lazy sequence over all parsers reachable from [root] using
 * a depth-first traversal over the connected parser graph.
 */
fun allParsers(root: Parser<*>): Sequence<Parser<*>> = sequence {
  val todo = mutableListOf(root)
  val seen = mutableSetOf(root)
  while (todo.isNotEmpty()) {
    val current = todo.removeLast()
    yield(current)
    for (child in current.children.asReversed()) {
      if (seen.add(child)) {
        todo.add(child)
      }
    }
  }
}
