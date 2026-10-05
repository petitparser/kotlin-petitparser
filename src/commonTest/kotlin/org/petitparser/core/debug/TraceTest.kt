package org.petitparser.core.debug

import org.petitparser.core.context.Output
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.misc.labeled
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TraceTest {
  @Test
  fun test_trace_success() {
    val identifier = char('a') + digit().star()
    val events = mutableListOf<TraceEvent>()
    val traced = trace(identifier, output = events::add)
    val result = traced.parse("a12")
    assertTrue(result is Output.Success)
    assertTrue(events.isNotEmpty())

    // First event is enter event (result is null)
    assertNull(events.first().result)
    assertEquals(0, events.first().level)
    assertTrue(events.first().toString().isNotEmpty())

    // Last event is exit event (result is not null)
    assertNotNull(events.last().result)
    assertTrue(events.last().toString().isNotEmpty())
  }

  @Test
  fun test_trace_default_output() {
    val traced = trace(char('a'))
    val result = traced.parse("a")
    assertTrue(result is Output.Success)
  }

  @Test
  fun test_trace_predicate() {
    val identifier = char('a').labeled("first") + digit().star().labeled("second")
    val events = mutableListOf<TraceEvent>()
    val traced = trace(identifier, output = events::add) { it.toString().contains("first") }
    traced.parse("a1")
    assertTrue(events.all { it.parser.toString().contains("first") })
  }

  @Test
  fun test_trace_event_level() {
    val rootEvent = object : TraceEvent {
      override val parent: TraceEvent? = null
      override val parser = char('a')
      override val context = org.petitparser.core.context.Input("")
      override val result = null
    }
    assertEquals(0, rootEvent.level)

    val childEvent = object : TraceEvent {
      override val parent = rootEvent
      override val parser = char('b')
      override val context = org.petitparser.core.context.Input("")
      override val result = null
    }
    assertEquals(1, childEvent.level)
  }
}
