package org.petitparser.core.debug

import org.petitparser.core.context.Output
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgressTest {
  @Test
  fun test_progress_success() {
    val identifier = char('a') + digit().star()
    val frames = mutableListOf<ProgressFrame>()
    val progressed = progress(identifier, output = frames::add)
    val result = progressed.parse("a123")
    assertTrue(result is Output.Success)
    assertTrue(frames.isNotEmpty())
    assertEquals(0, frames.first().position)
    assertTrue(frames.first().toString().startsWith("* "))
  }

  @Test
  fun test_progress_predicate() {
    val identifier = char('a') + digit().star()
    val frames = mutableListOf<ProgressFrame>()
    val progressed = progress(identifier, output = frames::add) { it.children.isEmpty() }
    progressed.parse("a12")
    assertTrue(frames.all { it.parser.children.isEmpty() })
  }

  @Test
  fun test_progress_default_output() {
    val progressed = progress(char('a'))
    val result = progressed.parse("a")
    assertTrue(result is Output.Success)
  }
}
