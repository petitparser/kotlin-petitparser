package org.petitparser.core.reflection

import org.petitparser.core.definition.ResolvableParser
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.CastListParser
import org.petitparser.core.parser.action.CastParser
import org.petitparser.core.parser.action.ConstantParser
import org.petitparser.core.parser.action.FlattenParser
import org.petitparser.core.parser.action.MapParser
import org.petitparser.core.parser.action.PermuteParser
import org.petitparser.core.parser.action.PickParser
import org.petitparser.core.parser.action.TokenParser
import org.petitparser.core.parser.action.WhereParser
import org.petitparser.core.parser.combinator.ChoiceParser
import org.petitparser.core.parser.combinator.SettableParser
import org.petitparser.core.parser.consumer.CharacterParser
import org.petitparser.core.parser.consumer.NewlineParser
import org.petitparser.core.parser.consumer.StringParser
import org.petitparser.core.parser.misc.FailureParser
import org.petitparser.core.parser.repeater.PossessiveRepeatingParser
import org.petitparser.core.parser.repeater.RepeatingCharacterParser
import org.petitparser.core.parser.repeater.RepeatingParser
import org.petitparser.core.parser.repeater.SeparatedParser

/** The type of a linter issue. */
enum class LinterType {
  INFO,
  WARNING,
  ERROR,
}

/** Encapsulates a single linter rule. */
abstract class LinterRule(
  val type: LinterType,
  val title: String,
) {
  /** Executes this rule using the provided [analyzer] on a [parser]. */
  abstract fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit)

  override fun toString(): String = "${this::class.simpleName ?: "LinterRule"}(type: $type, title: $title)"
}

/** Encapsulates a single linter issue. */
class LinterIssue(
  val rule: LinterRule,
  val parser: Parser<*>,
  val description: String,
) {
  val type: LinterType get() = rule.type
  val title: String get() = rule.title

  override fun toString(): String =
    "${this::class.simpleName ?: "LinterIssue"}(type: $type, title: $title, parser: $parser, description: $description)"
}

class CharacterRepeaterRule : LinterRule(LinterType.WARNING, "Character repeater") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is FlattenParser) {
      val repeating = parser.delegate
      if (repeating is PossessiveRepeatingParser<*>) {
        val character = repeating.delegate
        if (character is CharacterParser) {
          callback(
            LinterIssue(
              this,
              parser,
              "A flattened repeater ($repeating) that delegates to a character " +
                "parser ($character) can be much more efficiently implemented " +
                "using `starString`, `plusString`, `timesString`, or " +
                "`repeatString` that directly returns the underlying String " +
                "instead of an intermediate List.",
            ),
          )
        }
      }
    }
  }
}

class DuplicateParserRule : LinterRule(LinterType.INFO, "Duplicate parser") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    val duplicates = analyzer.parsers.filter { parser.isEqualTo(it) }
    if (duplicates.size > 1 && duplicates.first() === parser) {
      callback(
        LinterIssue(
          this,
          parser,
          "${duplicates.size} instances of the same parser exist in this " +
            "grammar. If possible, reuse the same parser instances to reduce " +
            "memory footprint and increase performance.",
        ),
      )
    }
  }
}

class LeftRecursionRule : LinterRule(LinterType.ERROR, "Left recursion") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    val cycle = analyzer.cycleSet(parser)
    if (cycle.isNotEmpty()) {
      callback(
        LinterIssue(
          this,
          parser,
          "The parsers directly or indirectly refers to itself without " +
            "consuming input:\n" +
            formatIterable(cycle, offset = 1) + "\n" +
            "This causes an infinite loop when parsing.",
        ),
      )
    }
  }
}

class NestedChoiceRule : LinterRule(LinterType.INFO, "Nested choice") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is ChoiceParser<*>) {
      val children = parser.children
      for (i in 0 until children.size - 1) {
        val child = children[i]
        if (child is ChoiceParser<*>) {
          callback(
            LinterIssue(
              this,
              parser,
              "The choice at index $i is another choice ($child) that adds " +
                "unnecessary overhead that can be avoided by flattening it into " +
                "the parent.",
            ),
          )
        }
      }
    }
  }
}

class NullableRepeaterRule : LinterRule(LinterType.ERROR, "Nullable repeater") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is RepeatingParser<*, *> && analyzer.isNullable(parser.delegate)) {
      if (parser is SeparatedParser<*, *> && !analyzer.isNullable(parser.separator)) {
        return
      }
      callback(
        LinterIssue(
          this,
          parser,
          "A repeater that delegates to a nullable parser causes an infinite loop when parsing.",
        ),
      )
    }
  }
}

class OverlappingChoiceRule : LinterRule(LinterType.INFO, "Overlapping choice") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is ChoiceParser<*>) {
      val children = parser.children
      for (i in children.indices) {
        val firstI = analyzer.firstSet(children[i])
        for (j in i + 1 until children.size) {
          val firstJ = analyzer.firstSet(children[j])
          if (isParserIterableEqual(firstI, firstJ)) {
            callback(
              LinterIssue(
                this,
                parser,
                "The choices at index $i and $j have overlapping first-sets, " +
                  "which can be an indication of an inefficient grammar:\n" +
                  formatIterable(firstI) + "\n" +
                  "If possible, try extracting common prefixes from choices.",
              ),
            )
          }
        }
      }
    }
  }
}

class RepeatedChoiceRule : LinterRule(LinterType.WARNING, "Repeated choice") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is ChoiceParser<*>) {
      val children = parser.children
      for (i in children.indices) {
        for (j in i + 1 until children.size) {
          if (children[i].isEqualTo(children[j])) {
            callback(
              LinterIssue(
                this,
                parser,
                "The choices at index $i and $j are identical:\n" +
                  " $i: ${children[i]}\n" +
                  " $j: ${children[j]}\n" +
                  "The second choice can never succeed and can therefore be removed.",
              ),
            )
          }
        }
      }
    }
  }
}

class UnnecessaryFlattenRule : LinterRule(LinterType.WARNING, "Unnecessary flatten") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is FlattenParser && parser.message == null) {
      val delegate = parser.delegate
      if (delegate is CharacterParser ||
        delegate is FlattenParser ||
        delegate is NewlineParser ||
        delegate is StringParser ||
        delegate is RepeatingCharacterParser
      ) {
        callback(
          LinterIssue(
            this,
            parser,
            "A flatten parser delegating to a parser ($delegate) that is " +
              "returning the accepted input string adds unnecessary overhead and " +
              "can be removed.",
          ),
        )
      }
    }
  }
}

class UnnecessaryResolvableRule : LinterRule(LinterType.WARNING, "Unnecessary resolvable") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is ResolvableParser) {
      callback(
        LinterIssue(
          this,
          parser,
          "Resolvable parsers are used during construction of recursive " +
            "grammars. While they typically dispatch to their delegate, " +
            "they add unnecessary overhead and can be avoided by removing " +
            "them before parsing using `resolve(parser)`.",
        ),
      )
    }
  }
}

class UnoptimizedFlattenRule : LinterRule(LinterType.INFO, "Unoptimized flatten") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is FlattenParser && parser.message == null) {
      callback(
        LinterIssue(
          this,
          parser,
          "A flatten parser without an error message is unable to switch " +
            "to the fast parsing mode. This can lead to inefficient parsers " +
            "and can usually easily fixed by providing an error message " +
            "that should be used in case the delegate fails to parse.",
        ),
      )
    }
  }
}

class UnreachableChoiceRule : LinterRule(LinterType.WARNING, "Unreachable choice") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is ChoiceParser<*>) {
      val children = parser.children
      for (i in 0 until children.size - 1) {
        if (analyzer.isNullable(children[i])) {
          callback(
            LinterIssue(
              this,
              parser,
              "The choice at index $i is nullable:\n" +
                " $i: ${children[i]}\n" +
                "thus the choices after that can never be reached and can be removed:\n" +
                formatIterable(children.subList(i + 1, children.size), offset = i + 1),
            ),
          )
        }
      }
    }
  }
}

class UnresolvedSettableRule : LinterRule(LinterType.ERROR, "Unresolved settable") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is SettableParser<*> && parser.delegate is FailureParser<*>) {
      callback(
        LinterIssue(
          this,
          parser,
          "This error is typically a bug in the code where a recursive " +
            "grammar was created with `undefined()` that has not been resolved.",
        ),
      )
    }
  }
}

class UnusedResultRule : LinterRule(LinterType.INFO, "Unused result") {
  override fun run(analyzer: Analyzer, parser: Parser<*>, callback: (LinterIssue) -> Unit) {
    if (parser is FlattenParser) {
      val deepChildren = analyzer.allChildren(parser)
      val ignoredResults = deepChildren.filter(::isResultProducing).toSet()
      if (ignoredResults.isNotEmpty()) {
        val path = analyzer.findPath(parser) { it.target in ignoredResults }
        if (path != null) {
          callback(
            LinterIssue(
              this,
              parser,
              "The flatten parser discards the result of its children and " +
                "instead returns the consumed input. Yet this flatten parser " +
                "(indirectly) refers to one or more other parsers that explicitly " +
                "produce a result which is then ignored when called from this context:\n" +
                formatIterable(path.parsers, offset = 1) + "\n" +
                "This might point to an inefficient grammar or a possible bug.",
            ),
          )
        }
      }
    }
  }

  private fun isResultProducing(parser: Parser<*>): Boolean =
    parser is CastParser<*, *> ||
      parser is CastListParser<*, *> ||
      parser is ConstantParser<*, *> ||
      parser is FlattenParser ||
      (parser is MapParser<*, *> && !parser.hasSideEffects) ||
      parser is PermuteParser<*> ||
      parser is PickParser<*> ||
      parser is TokenParser<*> ||
      parser is WhereParser<*>
}

/** All default linter rules to be run. */
val allLinterRules: List<LinterRule> = listOf(
  CharacterRepeaterRule(),
  DuplicateParserRule(),
  LeftRecursionRule(),
  NestedChoiceRule(),
  NullableRepeaterRule(),
  OverlappingChoiceRule(),
  RepeatedChoiceRule(),
  UnnecessaryFlattenRule(),
  UnnecessaryResolvableRule(),
  UnoptimizedFlattenRule(),
  UnreachableChoiceRule(),
  UnresolvedSettableRule(),
  UnusedResultRule(),
)

/**
 * Returns a list of linter issues found when analyzing the parser graph reachable from [parser].
 */
fun linter(
  parser: Parser<*>,
  callback: ((LinterIssue) -> Unit)? = null,
  rules: List<LinterRule>? = null,
  excludedRules: Set<String> = emptySet(),
  excludedTypes: Set<LinterType> = setOf(LinterType.INFO),
): List<LinterIssue> {
  val issues = mutableListOf<LinterIssue>()
  val analyzer = Analyzer(parser)
  val selectedRules = rules ?: allLinterRules.filter { rule ->
    rule.title !in excludedRules && rule.type !in excludedTypes
  }
  for (p in analyzer.parsers) {
    for (rule in selectedRules) {
      rule.run(analyzer, p) { issue ->
        callback?.invoke(issue)
        issues.add(issue)
      }
    }
  }
  return issues
}
