package org.petitparser.core.definition

import org.petitparser.core.parser.Parser

/**
 * Resolves all parser references reachable through [root].
 * Returns an optimized parser graph that inlines all references directly.
 */
@Suppress("UNCHECKED_CAST")
fun <R> resolve(root: Parser<R>): Parser<R> {
  val mapping = mutableMapOf<ResolvableParser<*>, Parser<*>>()
  val parser: Parser<R> = dereference(root, mapping)
  val todo = mutableListOf<Parser<*>>(parser)
  val seen = mutableSetOf<Parser<*>>(parser)
  while (todo.isNotEmpty()) {
    val parent = todo.removeLast()
    for (child in parent.children) {
      var actualChild = child
      if (actualChild is ResolvableParser<*>) {
        val referenced = dereference(actualChild, mapping)
        parent.replace(actualChild, referenced)
        actualChild = referenced
      }
      if (seen.add(actualChild)) {
        todo.add(actualChild)
      }
    }
  }
  return parser
}

@Suppress("UNCHECKED_CAST")
private fun <R> dereference(
  parser: Parser<R>,
  mapping: MutableMap<ResolvableParser<*>, Parser<*>>,
): Parser<R> {
  var current: Parser<*> = parser
  val references = mutableSetOf<ResolvableParser<*>>()
  while (current is ResolvableParser<*>) {
    val existing = mapping[current]
    if (existing != null) {
      current = existing
      break
    }
    if (!references.add(current)) {
      throw IllegalStateException("Recursive references detected: $references")
    }
    current = current.resolve()
  }
  for (reference in references) {
    mapping[reference] = current
  }
  return current as Parser<R>
}
