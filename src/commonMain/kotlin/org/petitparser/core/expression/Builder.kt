package org.petitparser.core.expression

import org.petitparser.core.expression.ExpressionGroup.Companion.buildChoice
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.combinator.SettableParser
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.reflection.allParsers

/**
 * A builder that allows the simple definition of expression grammars with
 * prefix, postfix, and left- and right-associative infix operators.
 */
class ExpressionBuilder<T> {
  private val primitives = mutableListOf<Parser<T>>()
  private val groups = mutableListOf<ExpressionGroup<T>>()
  private val settableLoopback: SettableParser<T> = undefined()

  /**
   * The parser for this expression builder.
   * Can be used to loop back to this parser.
   */
  val loopback: Parser<T> get() = settableLoopback

  /**
   * Defines a new primitive, literal, or value [parser].
   */
  fun primitive(parser: Parser<T>) {
    primitives.add(parser)
  }

  /**
   * Creates a new group of operators that share the same priority.
   */
  fun group(action: ExpressionGroup<T>.() -> Unit = {}): ExpressionGroup<T> {
    val group = ExpressionGroup<T>(settableLoopback)
    group.action()
    groups.add(group)
    return group
  }

  /**
   * Builds the expression parser.
   */
  fun build(): Parser<T> {
    var parser: Parser<T>? = if (primitives.isNotEmpty()) buildChoice(primitives) else null
    for (group in groups) {
      parser = group.build(parser)
    }
    val result = parser ?: throw IllegalStateException("At least one primitive parser expected")
    for (parent in allParsers(result).toList()) {
      parent.replace(settableLoopback, result)
    }
    settableLoopback.set(result)
    return result
  }
}

/**
 * Builds an expression parser using the [ExpressionBuilder] DSL.
 */
inline fun <T> buildExpression(builderAction: ExpressionBuilder<T>.() -> Unit): Parser<T> {
  val builder = ExpressionBuilder<T>()
  builder.builderAction()
  return builder.build()
}
