package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser

/**
 * Transforms all parsers reachable from [parser] with the given [handler].
 *
 * The identity function returns a copy of the incoming parser.
 *
 * The implementation first creates a copy of each parser reachable in the
 * input grammar; then the resulting grammar is traversed until all references
 * to old parsers are replaced with the transformed ones.
 */
@Suppress("UNCHECKED_CAST")
fun <R> transformParser(
  parser: Parser<R>,
  handler: (Parser<*>) -> Parser<*>,
): Parser<R> {
  val mapping = mutableMapOf<Parser<*>, Parser<*>>()
  for (each in allParsers(parser)) {
    mapping[each] = handler(each.copy())
  }
  val todo = mapping.values.toMutableList()
  val seen = mapping.values.toMutableSet()
  while (todo.isNotEmpty()) {
    val parent = todo.removeLast()
    for (child in parent.children) {
      val mapped = mapping[child]
      if (mapped != null) {
        parent.replace(child, mapped)
      } else if (seen.add(child)) {
        todo.add(child)
      }
    }
  }
  return (mapping[parser] ?: parser) as Parser<R>
}
