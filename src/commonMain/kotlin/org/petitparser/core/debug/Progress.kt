package org.petitparser.core.debug

import org.petitparser.core.context.Input
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.callCC
import org.petitparser.core.reflection.transformParser

/** Encapsulates the progress data of a parser invocation. */
interface ProgressFrame {
  val parser: Parser<*>
  val context: Input
  val position: Int get() = context.position
}

private class ProgressFrameImpl(
  override val parser: Parser<*>,
  override val context: Input,
) : ProgressFrame {
  override fun toString(): String = "${"*".repeat(1 + position)} $parser"
}

/**
 * Returns a transformed [Parser] that visually reports parse progress.
 */
fun <R> progress(
  root: Parser<R>,
  output: (ProgressFrame) -> Unit = ::println,
  predicate: ((Parser<*>) -> Boolean)? = null,
): Parser<R> = transformParser(root) { parser ->
  if (predicate == null || predicate(parser)) {
    parser.callCC { continuation, context ->
      output(ProgressFrameImpl(parser, context))
      continuation(context)
    }
  } else {
    parser
  }
}
