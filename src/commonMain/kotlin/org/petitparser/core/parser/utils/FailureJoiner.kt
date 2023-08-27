package org.petitparser.core.parser.utils

import org.petitparser.core.context.Output
import org.petitparser.core.context.failure

/** Function definition that joins parse [Output.Failure] instances. */
typealias FailureJoiner = (first: Output.Failure, second: Output.Failure) -> Output.Failure

/** Reports the first parse failure observed. */
fun selectFirst(
  first: Output.Failure,
  @Suppress("UNUSED_PARAMETER") second: Output.Failure,
) = first

/** Reports the last parse failure observed (default). */
fun selectLast(
  @Suppress("UNUSED_PARAMETER") first: Output.Failure,
  second: Output.Failure,
) = second

/** Reports the parser failure farthest down in the input string, preferring later failures over earlier ones. */
fun selectFarthest(first: Output.Failure, second: Output.Failure) =
  if (first.position <= second.position) second
  else first

/** Reports the parser failure farthest down in the input string, joining error messages at the same position. */
fun selectFarthestJoined(first: Output.Failure, second: Output.Failure) =
  if (first.position > second.position) first
  else if (first.position < second.position) second
  else first.failure("${first.message} OR ${second.message}")
