package org.petitparser.core.debug

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.callCC
import org.petitparser.core.reflection.transformParser

/** Encapsulates the entry and exit data around a parser trace. */
interface TraceEvent {
  val parent: TraceEvent?
  val parser: Parser<*>
  val context: Input
  val result: Output<*>?
  val level: Int get() = if (parent != null) parent!!.level + 1 else 0
}

private class TraceEventImpl(
  override val parent: TraceEvent?,
  override val parser: Parser<*>,
  override val context: Input,
  override val result: Output<*>? = null,
) : TraceEvent {
  override fun toString(): String {
    val indent = "  ".repeat(level)
    return "$indent${result ?: parser}"
  }
}

/**
 * Returns a transformed [Parser] that prints or records a trace of all activated parsers
 * and their respective parse results.
 */
fun <R> trace(
  root: Parser<R>,
  output: (TraceEvent) -> Unit = ::println,
  predicate: ((Parser<*>) -> Boolean)? = null,
): Parser<R> {
  var parent: TraceEvent? = null
  return transformParser(root) { parser ->
    if (predicate == null || predicate(parser)) {
      parser.callCC { continuation, context ->
        val currentParent = parent
        val enterEvent = TraceEventImpl(currentParent, parser, context)
        parent = enterEvent
        output(enterEvent)
        val result = continuation(context)
        val exitEvent = TraceEventImpl(currentParent, parser, context, result)
        output(exitEvent)
        parent = currentParent
        result
      }
    } else {
      parser
    }
  }
}
