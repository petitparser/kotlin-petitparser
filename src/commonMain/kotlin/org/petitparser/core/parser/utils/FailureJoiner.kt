package org.petitparser.core.parser.utils

import org.petitparser.core.context.Output

/** Function definition that joins parse [Output.Failure] instances. */
typealias FailureJoiner<R> = (first: Output.Failure<R>, second: Output.Failure<R>) -> Output.Failure<R>

/** Reports the first parse failure observed. */
fun <R> selectFirst(
  first: Output.Failure<R>,
  @Suppress("UNUSED_PARAMETER") second: Output.Failure<R>,
) = first

/** Reports the last parse failure observed (default). */
fun <R> selectLast(
  @Suppress("UNUSED_PARAMETER") first: Output.Failure<R>,
  second: Output.Failure<R>,
) = second

/** Reports the parser failure farthest down in the input string, preferring later failures over earlier ones. */
fun <R> selectFarthest(first: Output.Failure<R>, second: Output.Failure<R>) =
  if (first.position <= second.position) second
  else first

/** Reports the parser failure farthest down in the input string, joining error messages at the same position. */
fun <R> selectFarthestJoined(first: Output.Failure<R>, second: Output.Failure<R>) =
  if (first.position > second.position) first
  else if (first.position < second.position) second
  else first.failure("${first.message} OR ${second.message}")
