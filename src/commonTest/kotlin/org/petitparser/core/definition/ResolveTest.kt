package org.petitparser.core.definition

import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.consumer.char
import kotlin.test.Test

class ResolveTest {
  @Test
  fun test_resolve_simple() {
    val parser = ref { char('x') }
    val resolved = resolve(parser)
    assertSuccess(resolved, "x", 'x')
  }
}
