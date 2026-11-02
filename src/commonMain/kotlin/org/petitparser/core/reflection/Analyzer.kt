package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.OptionalParser
import org.petitparser.core.parser.misc.EpsilonParser
import org.petitparser.core.parser.misc.PositionParser
import org.petitparser.core.parser.repeater.RepeatingCharacterParser
import org.petitparser.core.parser.repeater.RepeatingParser
import org.petitparser.core.parser.utils.SequentialParser

/** A continuous path through the parser graph. */
class ParserPath(
  val parsers: List<Parser<*>>,
  val indexes: List<Int>,
) {
  init {
    require(parsers.isNotEmpty()) { "parsers cannot be empty" }
    require(indexes.size == parsers.size - 1) { "indexes wrong size" }
    for (i in indexes.indices) {
      require(parsers[i].children[indexes[i]] == parsers[i + 1]) { "indexes invalid" }
    }
  }

  val source: Parser<*> get() = parsers.first()
  val target: Parser<*> get() = parsers.last()
  val length: Int get() = parsers.size

  override fun toString(): String = "ParserPath(parsers=$parsers, indexes=$indexes)"
}

/** Performs a depth-first search for paths satisfying [predicate]. */
fun depthFirstSearch(
  path: ParserPath,
  predicate: (ParserPath) -> Boolean,
): Sequence<ParserPath> = sequence {
  val parsers = path.parsers.toMutableList()
  val indexes = path.indexes.toMutableList()

  suspend fun SequenceScope<ParserPath>.dfs() {
    val currentPath = ParserPath(parsers.toList(), indexes.toList())
    if (predicate(currentPath)) {
      yield(currentPath)
    } else {
      val children = parsers.last().children
      for (i in children.indices) {
        val child = children[i]
        if (child !in parsers) {
          parsers.add(child)
          indexes.add(i)
          dfs()
          parsers.removeLast()
          indexes.removeLast()
        }
      }
    }
  }

  dfs()
}

/** Returns `true` if [parser] is directly nullable. */
fun isNullable(parser: Parser<*>): Boolean =
  parser is OptionalParser<*> ||
    parser is EpsilonParser<*> ||
    parser is PositionParser ||
    (parser is RepeatingParser<*, *> && parser.min == 0) ||
    (parser is RepeatingCharacterParser && parser.min == 0)

/** Returns `true` if [parser] is a terminal or leaf parser. */
fun isTerminal(parser: Parser<*>): Boolean = parser.children.isEmpty()

/** Returns `true` if [parser] consumes its children in the declared sequence. */
fun isSequence(parser: Parser<*>): Boolean =
  parser is SequentialParser && parser.children.size > 1

/** Adds all [elements] to [result]. Returns `true` if [result] was changed. */
fun <T> addAll(result: MutableSet<T>, elements: Iterable<T>): Boolean {
  var changed = false
  for (element in elements) {
    changed = changed or result.add(element)
  }
  return changed
}

/** Tests if two sets of parsers are structurally equal. */
fun isParserIterableEqual(first: Iterable<Parser<*>>, second: Iterable<Parser<*>>): Boolean {
  for (one in first) {
    if (second.none { one.isEqualTo(it) }) return false
  }
  for (two in second) {
    if (first.none { two.isEqualTo(it) }) return false
  }
  return true
}

/** Generates a human-readable list of strings. */
fun <T> formatIterable(objects: Iterable<T>, offset: Int? = null): String {
  val buffer = StringBuilder()
  for ((i, element) in objects.withIndex()) {
    if (0 < i) buffer.append('\n')
    if (offset != null) {
      buffer.append(" ${offset + i}: ")
    } else {
      buffer.append(" - ")
    }
    buffer.append(element)
  }
  return buffer.toString()
}

/** Computes the first-sets for [parsers]. */
fun computeFirstSets(
  parsers: Iterable<Parser<*>>,
  sentinel: Parser<*>,
): Map<Parser<*>, Set<Parser<*>>> {
  val firstSets = parsers.associateWith { parser ->
    val set = mutableSetOf<Parser<*>>()
    if (isTerminal(parser)) set.add(parser)
    if (isNullable(parser)) set.add(sentinel)
    set
  }.toMutableMap()
  var changed: Boolean
  do {
    changed = false
    for (parser in parsers) {
      changed = changed or expandFirstSet(parser, firstSets, sentinel)
    }
  } while (changed)
  return firstSets
}

fun expandFirstSet(
  parser: Parser<*>,
  firstSets: MutableMap<Parser<*>, MutableSet<Parser<*>>>,
  sentinel: Parser<*>,
): Boolean {
  var changed = false
  val firstSet = firstSets.getValue(parser)
  if (isSequence(parser)) {
    for (child in parser.children) {
      var nullable = false
      for (first in firstSets.getValue(child)) {
        if (isNullable(first)) {
          nullable = true
        } else {
          changed = changed or firstSet.add(first)
        }
      }
      if (!nullable) {
        return changed
      }
    }
    changed = changed or firstSet.add(sentinel)
  } else {
    for (child in parser.children) {
      changed = changed or addAll(firstSet, firstSets.getValue(child))
    }
  }
  return changed
}

/** Computes the follow-sets for [parsers]. */
fun computeFollowSets(
  root: Parser<*>,
  parsers: Iterable<Parser<*>>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
  sentinel: Parser<*>,
): Map<Parser<*>, Set<Parser<*>>> {
  val followSets = parsers.associateWith { parser ->
    val set = mutableSetOf<Parser<*>>()
    if (parser == root) set.add(sentinel)
    set
  }.toMutableMap()
  var changed: Boolean
  do {
    changed = false
    for (parser in parsers) {
      changed = changed or expandFollowSet(parser, followSets, firstSets)
    }
  } while (changed)
  return followSets
}

fun expandFollowSet(
  parser: Parser<*>,
  followSets: MutableMap<Parser<*>, MutableSet<Parser<*>>>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
): Boolean {
  return if (isSequence(parser)) {
    expandFollowSetOfSequence(parser, parser.children, followSets, firstSets)
  } else if (parser is RepeatingParser<*, *>) {
    expandFollowSetOfSequence(
      parser,
      listOf(parser.children[0]) + parser.children,
      followSets,
      firstSets,
    )
  } else {
    var changed = false
    for (child in parser.children) {
      changed = changed or addAll(followSets.getValue(child), followSets.getValue(parser))
    }
    changed
  }
}

fun expandFollowSetOfSequence(
  parser: Parser<*>,
  children: List<Parser<*>>,
  followSets: MutableMap<Parser<*>, MutableSet<Parser<*>>>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
): Boolean {
  var changed = false
  for (i in children.indices) {
    if (i == children.size - 1) {
      changed = changed or addAll(followSets.getValue(children[i]), followSets.getValue(parser))
    } else {
      val firstSet = mutableSetOf<Parser<*>>()
      var j = i + 1
      while (j < children.size) {
        firstSet.addAll(firstSets.getValue(children[j]))
        if (firstSets.getValue(children[j]).none(::isNullable)) {
          break
        }
        j++
      }
      if (j == children.size) {
        changed = changed or addAll(followSets.getValue(children[i]), followSets.getValue(parser))
      }
      changed = changed or addAll(
        followSets.getValue(children[i]),
        firstSet.filter { !isNullable(it) },
      )
    }
  }
  return changed
}

/** Computes cycle-sets for [parsers]. */
fun computeCycleSets(
  parsers: Iterable<Parser<*>>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
): Map<Parser<*>, List<Parser<*>>> {
  val cycleSets = mutableMapOf<Parser<*>, List<Parser<*>>>()
  for (parser in parsers) {
    computeCycleSet(parser, firstSets, cycleSets)
  }
  return cycleSets
}

fun computeCycleSet(
  parser: Parser<*>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
  cycleSets: MutableMap<Parser<*>, List<Parser<*>>>,
  stack: MutableList<Parser<*>> = mutableListOf(parser),
) {
  if (parser in cycleSets) return
  if (isTerminal(parser)) {
    cycleSets[parser] = emptyList()
    return
  }
  val children = computeCycleChildren(parser, firstSets)
  for (child in children) {
    val index = stack.indexOf(child)
    if (index >= 0) {
      val cycle = stack.subList(index, stack.size).toList()
      for (p in cycle) {
        cycleSets[p] = cycle
      }
      return
    } else {
      stack.add(child)
      computeCycleSet(child, firstSets, cycleSets, stack)
      stack.removeLast()
    }
  }
  if (parser !in cycleSets) {
    cycleSets[parser] = emptyList()
  }
}

fun computeCycleChildren(
  parser: Parser<*>,
  firstSets: Map<Parser<*>, Set<Parser<*>>>,
): List<Parser<*>> {
  if (isSequence(parser)) {
    val children = mutableListOf<Parser<*>>()
    for (child in parser.children) {
      children.add(child)
      if (firstSets.getValue(child).none(::isNullable)) {
        break
      }
    }
    return children
  }
  return parser.children
}

/** Helper to reflect on properties of a grammar. */
class Analyzer(val root: Parser<*>) {
  /** The set of all parsers reachable from [root]. */
  val parsers: Set<Parser<*>> by lazy { allParsers(root).toSet() }

  private val allChildrenMap = mutableMapOf<Parser<*>, Set<Parser<*>>>()

  /** Returns a set of all deep children reachable from [parser]. */
  fun allChildren(parser: Parser<*>): Set<Parser<*>> {
    require(parser in parsers) { "parser is not part of the analyzer" }
    return allChildrenMap.getOrPut(parser) {
      parser.children.flatMapTo(mutableSetOf()) { child -> allParsers(child) }
    }
  }

  /** Returns the shortest path from [source] that satisfies [predicate], if any. */
  fun findPath(source: Parser<*>, predicate: (ParserPath) -> Boolean): ParserPath? =
    findAllPaths(source, predicate).minByOrNull { it.length }

  /** Returns the shortest path from [source] to [target], if any. */
  fun findPathTo(source: Parser<*>, target: Parser<*>): ParserPath? {
    require(target in parsers) { "target is not part of the analyzer" }
    return findPath(source) { it.target == target }
  }

  /** Returns all paths starting at [source] that satisfy [predicate]. */
  fun findAllPaths(
    source: Parser<*>,
    predicate: (ParserPath) -> Boolean,
  ): Sequence<ParserPath> {
    require(source in parsers) { "source is not part of the analyzer" }
    return depthFirstSearch(ParserPath(listOf(source), emptyList()), predicate)
  }

  /** Returns all paths starting at [source] that end in [target]. */
  fun findAllPathsTo(source: Parser<*>, target: Parser<*>): Sequence<ParserPath> {
    require(target in parsers) { "target is not part of the analyzer" }
    return findAllPaths(source) { it.target == target }
  }

  /** Returns `true` if [parser] is transitively nullable. */
  fun isNullable(parser: Parser<*>): Boolean =
    firstSets.getValue(parser).contains(sentinel)

  /** Returns the first-set of [parser]. */
  fun firstSet(parser: Parser<*>): Set<Parser<*>> =
    firstSets.getValue(parser)

  private val firstSets: Map<Parser<*>, Set<Parser<*>>> by lazy {
    computeFirstSets(parsers, sentinel)
  }

  /** Returns the follow-set of [parser]. */
  fun followSet(parser: Parser<*>): Set<Parser<*>> =
    followSets.getValue(parser)

  private val followSets: Map<Parser<*>, Set<Parser<*>>> by lazy {
    computeFollowSets(root, parsers, firstSets, sentinel)
  }

  /** Returns the cycle-set of [parser]. */
  fun cycleSet(parser: Parser<*>): List<Parser<*>> =
    cycleSets.getValue(parser)

  private val cycleSets: Map<Parser<*>, List<Parser<*>>> by lazy {
    computeCycleSets(parsers, firstSets)
  }

  companion object {
    /** Sentinel parser used to denote epsilon/nullable in first and follow set computations. */
    val sentinel: Parser<*> = EpsilonParser<Any?>(null)
  }
}
