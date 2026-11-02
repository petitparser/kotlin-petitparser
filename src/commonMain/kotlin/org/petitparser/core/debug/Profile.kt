package org.petitparser.core.debug

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.callCC
import org.petitparser.core.reflection.transformParser
import kotlin.time.Duration
import kotlin.time.TimeSource

/** Encapsulates the profiling data for a parser. */
interface ProfileFrame {
  val parser: Parser<*>
  val count: Int
  val elapsed: Duration
}

private class ProfileFrameImpl(
  override val parser: Parser<*>,
) : ProfileFrame {
  override var count: Int = 0
  override var elapsed: Duration = Duration.ZERO

  override fun toString(): String = "$count\t${elapsed.inWholeMicroseconds}\t$parser"
}

/**
 * Returns a transformed [Parser] that measures activation count and total elapsed time.
 */
fun <R> profile(
  root: Parser<R>,
  output: (ProfileFrame) -> Unit = ::println,
  predicate: ((Parser<*>) -> Boolean)? = null,
): Parser<R> {
  val frames = mutableListOf<ProfileFrameImpl>()
  val timeSource = TimeSource.Monotonic
  val transformed = transformParser(root) { parser ->
    if (predicate == null || predicate(parser)) {
      val frame = ProfileFrameImpl(parser)
      frames.add(frame)
      parser.callCC { continuation, context ->
        frame.count++
        val mark = timeSource.markNow()
        val result = continuation(context)
        frame.elapsed += mark.elapsedNow()
        result
      }
    } else {
      parser
    }
  }
  return transformed.callCC { continuation, context ->
    val result = continuation(context)
    frames.forEach(output)
    result
  }
}
