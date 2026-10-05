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

class ProfileTest {
  @Test
  fun test_profile_success() {
    val identifier = char('a') + digit().star()
    val frames = mutableListOf<ProfileFrame>()
    val profiled = profile(identifier, output = frames::add)
    val result = profiled.parse("a123")
    assertTrue(result is Output.Success)
    assertTrue(frames.isNotEmpty())
    val rootFrame = frames.first { it.parser::class == identifier::class }
    assertEquals(1, rootFrame.count)
    assertTrue(rootFrame.toString().contains("SequenceParser"))
  }

  @Test
  fun test_profile_predicate() {
    val identifier = char('a') + digit().star()
    val frames = mutableListOf<ProfileFrame>()
    val profiled = profile(identifier, output = frames::add) { it.children.isEmpty() }
    profiled.parse("a1")
    assertTrue(frames.all { it.parser.children.isEmpty() })
  }

  @Test
  fun test_profile_default_output() {
    val profiled = profile(char('a'))
    val result = profiled.parse("a")
    assertTrue(result is Output.Success)
  }
}
