package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.FlattenParser
import org.petitparser.core.parser.combinator.ChoiceParser
import org.petitparser.core.parser.combinator.DelegateParser
import org.petitparser.core.parser.combinator.SettableParser
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.consumer.CharacterParser
import org.petitparser.core.parser.misc.LabeledParser
import org.petitparser.core.parser.repeater.PossessiveRepeatingParser
import org.petitparser.core.parser.repeater.RepeatingCharacterParser

/** Function signature of a replacement callback. */
typealias ReplaceParser<R> = (source: Parser<R>, target: Parser<R>) -> Unit

/** Encapsulates a single optimization rule. */
abstract class OptimizeRule {
  /** Executes this rule using the provided [analyzer] on a [parser]. */
  abstract fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>)
}

/** Rule that replaces a flattened repeater of characters with a [RepeatingCharacterParser]. */
class CharacterRepeater : OptimizeRule() {
  @Suppress("UNCHECKED_CAST")
  override fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>) {
    if (parser is FlattenParser) {
      val repeating = parser.delegate
      if (repeating is PossessiveRepeatingParser<*>) {
        val character = repeating.delegate
        if (character is CharacterParser) {
          replace(
            parser,
            RepeatingCharacterParser(
              character.predicate,
              character.message,
              repeating.min,
              repeating.max,
            ) as Parser<R>,
          )
        }
      }
    }
  }
}

/** Rule that flattens nested choices with the same failure joiner into the parent choice. */
class FlattenChoice : OptimizeRule() {
  @Suppress("UNCHECKED_CAST")
  override fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>) {
    if (parser is ChoiceParser<*>) {
      val children = parser.children.flatMap { child ->
        if (child is ChoiceParser<*> && parser.failureJoiner == child.failureJoiner) {
          child.children
        } else {
          listOf(child)
        }
      }
      if (parser.children.size < children.size) {
        replace(
          parser,
          (children as List<Parser<R>>).toChoiceParser(failureJoiner = parser.failureJoiner),
        )
      }
    }
  }
}

/** Rule that removes redundant settable and labeled delegate parsers. */
class RemoveDelegate : OptimizeRule() {
  @Suppress("UNCHECKED_CAST")
  override fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>) {
    var current: Parser<R> = parser
    val delegates = mutableSetOf<Parser<R>>()
    while (current is DelegateParser<*, *> &&
      (current is SettableParser<*> || current is LabeledParser<*>)
    ) {
      if (!delegates.add(current)) {
        break // The grammar is looping.
      }
      current = (current as DelegateParser<R, *>).delegate
    }
    for (delegate in delegates) {
      replace(delegate, current)
    }
  }
}

/** Rule that unifies structurally identical parsers. */
class RemoveDuplicate : OptimizeRule() {
  @Suppress("UNCHECKED_CAST")
  override fun <R> run(analyzer: Analyzer, parser: Parser<R>, replace: ReplaceParser<R>) {
    val other = analyzer.parsers.firstOrNull { parser.isEqualTo(it) } ?: parser
    if (parser !== other) {
      replace(parser, other as Parser<R>)
    }
  }
}

/** All default optimizer rules to be run. */
val allOptimizerRules: List<OptimizeRule> = listOf(
  CharacterRepeater(),
  FlattenChoice(),
  RemoveDelegate(),
  RemoveDuplicate(),
)

/** Returns an in-place optimized version of the parser. */
@Suppress("UNCHECKED_CAST")
fun <R> optimize(
  parser: Parser<R>,
  callback: ReplaceParser<*>? = null,
  rules: List<OptimizeRule> = allOptimizerRules,
): Parser<R> {
  val analyzer = Analyzer(parser)
  val replacements = mutableMapOf<Parser<*>, Parser<*>>()
  for (each in analyzer.parsers) {
    for (rule in rules) {
      rule.run(analyzer, each) { source, target ->
        callback?.invoke(source, target)
        replacements[source] = target
      }
    }
  }
  if (replacements.isNotEmpty()) {
    for (each in analyzer.parsers) {
      for ((source, target) in replacements) {
        each.replace(source, target)
      }
    }
    return (replacements[parser] ?: parser) as Parser<R>
  }
  return parser
}
