package org.petitparser.core.definition

import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductionDelegateTest {
  @Test
  fun test_production_delegate() {
    val delegate = def { char('a') }
    val parser = delegate.resolve()
    assertTrue(char('a').isEqualTo(parser))
    assertTrue(delegate.toString().contains("ProductionDelegate"))
  }
}
