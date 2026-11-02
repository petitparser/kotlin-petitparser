package org.petitparser.core.definition

import org.petitparser.core.parser.consumer.char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ProductionDelegateTest {
  class SampleGrammar {
    val aDelegate = def { char('a') }
    val a by aDelegate

    val bDelegate = rule { char('b') }
    val b by bDelegate
  }

  class AnotherGrammar {
    val aDelegate = def { char('a') }
    val a by aDelegate
  }

  @Test
  fun test_production_delegate() {
    val delegate = def { char('a') }
    val parser = delegate.resolve()
    assertTrue(char('a').isEqualTo(parser))
    assertEquals("ProductionDelegate", delegate.toString())
    assertSame(delegate, delegate.copy())
    assertEquals(1, delegate.fastParseOn("a", 0))
    assertTrue(delegate.hasEqualProperties(delegate))
  }

  @Test
  fun test_grammar_delegates_and_rule_alias() {
    val g1 = SampleGrammar()
    val g2 = SampleGrammar()
    val g3 = AnotherGrammar()

    val d1 = g1.aDelegate
    val d2 = g2.aDelegate
    val d1b = g1.bDelegate
    val d3 = g3.aDelegate
    val standalone = def { char('a') }

    assertEquals("a", d1.name)
    assertSame(g1, d1.owner)
    assertEquals("ProductionDelegate(a)", d1.toString())

    assertEquals(d1, d1)
    assertFalse(d1.equals("other"))
    assertFalse(d1.equals(d2)) // different owner instance
    assertFalse(d1.equals(d1b)) // different name
    assertFalse(d1.equals(d3)) // different owner class
    assertFalse(standalone.equals(d1)) // owner is null
    assertFalse(d1.equals(standalone))

    assertTrue(d1.hashCode() != 0)
    assertTrue(standalone.hashCode() != 0)
    assertTrue(d1.hasEqualProperties(d1))
    assertFalse(d1.hasEqualProperties(d2))
  }
}
