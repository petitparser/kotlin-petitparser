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

/**
 * Returns a lazy sequence of all direct and indirect children reachable from [root].
 * The sequence only includes [root] itself if [root] is recursively reachable from its children.
 */
fun allChildren(root: Parser<*>): Sequence<Parser<*>> = sequence {
  val todo = mutableListOf<Parser<*>>()
  val seen = mutableSetOf<Parser<*>>()
  for (child in root.children.asReversed()) {
    if (seen.add(child)) {
      todo.add(child)
    }
  }
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

