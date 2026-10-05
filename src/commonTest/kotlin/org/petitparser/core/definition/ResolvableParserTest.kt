package org.petitparser.core.definition

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals

class ResolvableParserTest {
  @Test
  fun test_resolvable_parser() {
    val delegate = char('a')
    val resolvable = object : ResolvableParser<Char> {
      override fun resolve(): Parser<Char> = delegate
      override fun parseOn(input: Input): Output<Char> = delegate.parseOn(input)
    }
    assertEquals(delegate, resolvable.resolve())
  }
}
