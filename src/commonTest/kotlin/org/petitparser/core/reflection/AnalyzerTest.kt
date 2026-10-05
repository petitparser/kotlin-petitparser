package org.petitparser.core.reflection

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.accept
import org.petitparser.core.parser.combinator.SettableParser
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqOf
import org.petitparser.core.parser.combinator.toChoiceParser
import org.petitparser.core.parser.combinator.undefined
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.misc.epsilon
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AnalyzerTest {
  private fun createUebersetzerbau(): Map<String, Parser<*>> {
    val grammar = mutableMapOf<String, Parser<*>>()
    grammar["a"] = char('a')
    grammar["b"] = char('b')
    grammar["c"] = char('c')
    grammar["d"] = char('d')
    grammar["e"] = epsilon()
    grammar["B"] = grammar["b"]!! or grammar["e"]!!
    grammar["A"] = grammar["a"]!! or grammar["B"]!!
    grammar["S"] = seqOf(grammar["A"]!!, grammar["B"]!!, grammar["c"]!!, grammar["d"]!!)
    return grammar
  }

  private fun createDragon(): Map<String, SettableParser<*>> {
    val grammar = listOf("E", "Ep", "T", "Tp", "F").associateWith { undefined<Any?>() }
    grammar["E"]!!.set(seqOf(grammar["T"]!!, grammar["Ep"]!!))
    grammar["Ep"]!!.set(seqOf(char('+'), grammar["T"]!!, grammar["Ep"]!!).optional())
    grammar["T"]!!.set(seqOf(grammar["F"]!!, grammar["Tp"]!!))
    grammar["Tp"]!!.set(seqOf(char('*'), grammar["F"]!!, grammar["Tp"]!!).optional())
    grammar["F"]!!.set((char('(').seq(grammar["E"]!!).seq(char(')'))) or char('i'))
    return grammar
  }

  private fun createAmbiguous(): Map<String, SettableParser<*>> {
    val grammar = listOf("S", "A", "a", "B", "b").associateWith { undefined<Any?>() }
    grammar["S"]!!.set((grammar["A"]!!.seq(grammar["B"]!!)) or grammar["a"]!!)
    grammar["A"]!!.set((grammar["S"]!!.seq(grammar["B"]!!)) or grammar["b"]!!)
    grammar["a"]!!.set(char('a'))
    grammar["B"]!!.set((grammar["B"]!!.seq(grammar["A"]!!)) or grammar["a"]!!)
    grammar["b"]!!.set(char('b'))
    return grammar
  }

  private fun createRecursive(): Map<String, SettableParser<*>> {
    val grammar = listOf("S", "P", "p", "+").associateWith { undefined<Any?>() }
    grammar["S"]!!.set(grammar["P"]!! or grammar["p"]!!)
    grammar["P"]!!.set(seqOf(grammar["S"]!!, grammar["+"]!!, grammar["S"]!!))
    grammar["p"]!!.set(char('p'))
    grammar["+"]!!.set(char('+'))
    return grammar
  }

  private fun createSelfReference(): Parser<*> {
    val parser = undefined<Any?>()
    parser.set(parser)
    return parser
  }

  private fun expectTerminals(parsers: Iterable<Parser<*>>, inputs: Iterable<String>) {
    val expectedInputs = inputs.toSet()
    val actualInputs = mutableSetOf<String>()
    val chars = (32..126).map { it.toChar().toString() } + listOf("")
    for (parser in parsers) {
      val ended = parser.end()
      for (char in chars) {
        if (ended.accept(char)) {
          actualInputs.add(char)
        }
      }
    }
    assertEquals(expectedInputs, actualInputs)
  }

  @Test
  fun test_root() {
    val parser = char('a').plus()
    val analyzer = Analyzer(parser)
    assertSame(parser, analyzer.root)
  }

  @Test
  fun test_parsers() {
    val parser = char('a').plus()
    val analyzer = Analyzer(parser)
    assertEquals(setOf(parser, parser.children.first()), analyzer.parsers)
  }

  @Test
  fun test_allChildren_single() {
    val inner = char('a')
    val parser = inner.plus()
    val analyzer = Analyzer(parser)
    assertEquals(setOf(inner), analyzer.allChildren(parser))
    assertTrue(analyzer.allChildren(inner).isEmpty())
  }

  @Test
  fun test_allChildren_multiple() {
    val inner1 = char('a')
    val inner2 = char('b')
    val parser = inner1.seq(inner2)
    val analyzer = Analyzer(parser)
    assertEquals(setOf(inner1, inner2), analyzer.allChildren(parser))
    assertTrue(analyzer.allChildren(inner1).isEmpty())
    assertTrue(analyzer.allChildren(inner2).isEmpty())
  }

  @Test
  fun test_allChildren_repeated() {
    val inner1 = char('a')
    val inner2 = char('b')
    val parser = inner1 or inner2 or inner2
    val analyzer = Analyzer(parser)
    assertEquals(setOf(inner1, inner2), analyzer.allChildren(parser))
    assertTrue(analyzer.allChildren(inner1).isEmpty())
    assertTrue(analyzer.allChildren(inner2).isEmpty())
  }

  @Test
  fun test_allChildren_recursive() {
    val inner1 = char('a')
    val inner2 = undefined<Char>()
    val parser = listOf(inner1, inner2).toChoiceParser()
    inner2.set(parser)
    val analyzer = Analyzer(parser)
    assertEquals(setOf(inner1, inner2, parser), analyzer.allChildren(parser))
    assertTrue(analyzer.allChildren(inner1).isEmpty())
    assertEquals(setOf(inner1, inner2, parser), analyzer.allChildren(inner2))
  }

  @Test
  fun test_allChildren_uebersetzerbau() {
    val parsers = createUebersetzerbau()
    val analyzer = Analyzer(parsers["S"]!!)
    assertEquals(
      setOf(
        parsers["A"],
        parsers["B"],
        parsers["a"],
        parsers["b"],
        parsers["c"],
        parsers["d"],
        parsers["e"],
      ),
      analyzer.allChildren(parsers["S"]!!),
    )
    assertEquals(
      setOf(parsers["B"], parsers["a"], parsers["b"], parsers["e"]),
      analyzer.allChildren(parsers["A"]!!),
    )
    assertEquals(setOf(parsers["b"], parsers["e"]), analyzer.allChildren(parsers["B"]!!))
    assertTrue(analyzer.allChildren(parsers["a"]!!).isEmpty())
    assertTrue(analyzer.allChildren(parsers["b"]!!).isEmpty())
    assertTrue(analyzer.allChildren(parsers["c"]!!).isEmpty())
    assertTrue(analyzer.allChildren(parsers["d"]!!).isEmpty())
    assertTrue(analyzer.allChildren(parsers["e"]!!).isEmpty())
  }

  @Test
  fun test_allChildren_recursiveGrammar() {
    val parsers = createRecursive()
    val analyzer = Analyzer(parsers["S"]!!)
    assertEquals(analyzer.parsers, analyzer.allChildren(parsers["S"]!!))
    assertEquals(analyzer.parsers, analyzer.allChildren(parsers["P"]!!))
    assertEquals(setOf(parsers["p"]!!.children.first()), analyzer.allChildren(parsers["p"]!!))
    assertEquals(setOf(parsers["+"]!!.children.first()), analyzer.allChildren(parsers["+"]!!))
  }

  @Test
  fun test_allChildren_selfReference() {
    val parser = createSelfReference()
    val analyzer = Analyzer(parser)
    assertEquals(setOf(parser), analyzer.allChildren(parser))
  }

  @Test
  fun test_findPath_simple() {
    val parser = char('a')
    val analyzer = Analyzer(parser)
    val path = analyzer.findPathTo(parser, parser)
    assertNotNull(path)
    assertSame(parser, path.source)
    assertSame(parser, path.target)
    assertEquals(listOf(parser), path.parsers)
    assertTrue(path.indexes.isEmpty())
    val paths = analyzer.findAllPathsTo(parser, parser).toList()
    assertEquals(1, paths.size)
    assertEquals(listOf(parser), paths[0].parsers)
    assertTrue(paths[0].indexes.isEmpty())
  }

  @Test
  fun test_findPath_choice() {
    val terminal = char('a')
    val parser = terminal or terminal
    val analyzer = Analyzer(parser)
    val path = analyzer.findPathTo(parser, terminal)
    assertNotNull(path)
    assertSame(parser, path.source)
    assertSame(terminal, path.target)
    assertEquals(listOf(parser, terminal), path.parsers)
    assertEquals(listOf(0), path.indexes)
    val paths = analyzer.findAllPathsTo(parser, terminal).toList()
    assertEquals(2, paths.size)
    assertEquals(listOf(parser, terminal), paths[0].parsers)
    assertEquals(listOf(0), paths[0].indexes)
    assertEquals(listOf(parser, terminal), paths[1].parsers)
    assertEquals(listOf(1), paths[1].indexes)
  }

  @Test
  fun test_findPath_length() {
    val terminal = char('a')
    val repeated = terminal.star()
    val parser = repeated or terminal
    val analyzer = Analyzer(parser)
    val path = analyzer.findPathTo(parser, terminal)
    assertNotNull(path)
    assertSame(parser, path.source)
    assertSame(terminal, path.target)
    assertEquals(listOf(parser, terminal), path.parsers)
    assertEquals(listOf(1), path.indexes)
    val paths = analyzer.findAllPathsTo(parser, terminal).toList()
    assertEquals(2, paths.size)
    assertEquals(listOf(parser, repeated, terminal), paths[0].parsers)
    assertEquals(listOf(0, 0), paths[0].indexes)
    assertEquals(listOf(parser, terminal), paths[1].parsers)
    assertEquals(listOf(1), paths[1].indexes)
  }

  @Test
  fun test_findPath_recursiveGrammar() {
    val parsers = createRecursive()
    val analyzer = Analyzer(parsers["S"]!!)
    assertTrue(analyzer.findAllPaths(analyzer.root) { false }.toList().isEmpty())
  }

  @Test
  fun test_findPath_selfReference() {
    val parser = createSelfReference()
    val analyzer = Analyzer(parser)
    assertTrue(analyzer.findAllPaths(analyzer.root) { false }.toList().isEmpty())
  }

  @Test
  fun test_isNullable() {
    val a = char('a')
    val b = char('b')
    val plus = a.plus()
    assertFalse(Analyzer(plus).isNullable(plus))
    val star = a.star()
    assertTrue(Analyzer(star).isNullable(star))
    val opt = a.optional()
    assertTrue(Analyzer(opt).isNullable(opt))
    val choice = a or b
    assertFalse(Analyzer(choice).isNullable(choice))
    val epsChoice = a or epsilon()
    assertTrue(Analyzer(epsChoice).isNullable(epsChoice))
    val seq = a.seq(b)
    assertFalse(Analyzer(seq).isNullable(seq))
    val epsSeq = epsilon().seq(a)
    assertFalse(Analyzer(epsSeq).isNullable(epsSeq))
    val optSeq = a.optional().seq(b)
    assertFalse(Analyzer(optSeq).isNullable(optSeq))
  }

  @Test
  fun test_isNullable_uebersetzerbau() {
    val parsers = createUebersetzerbau()
    val analyzer = Analyzer(parsers["S"]!!)
    assertFalse(analyzer.isNullable(parsers["S"]!!))
    assertTrue(analyzer.isNullable(parsers["A"]!!))
    assertTrue(analyzer.isNullable(parsers["B"]!!))
    assertFalse(analyzer.isNullable(parsers["a"]!!))
    assertFalse(analyzer.isNullable(parsers["b"]!!))
    assertFalse(analyzer.isNullable(parsers["c"]!!))
    assertFalse(analyzer.isNullable(parsers["d"]!!))
    assertTrue(analyzer.isNullable(parsers["e"]!!))
  }

  @Test
  fun test_isNullable_dragon() {
    val parsers = createDragon()
    val analyzer = Analyzer(parsers["E"]!!)
    assertFalse(analyzer.isNullable(parsers["E"]!!))
    assertTrue(analyzer.isNullable(parsers["Ep"]!!))
    assertFalse(analyzer.isNullable(parsers["T"]!!))
    assertTrue(analyzer.isNullable(parsers["Tp"]!!))
    assertFalse(analyzer.isNullable(parsers["F"]!!))
  }

  @Test
  fun test_isNullable_ambiguous() {
    val parsers = createAmbiguous()
    val analyzer = Analyzer(parsers["S"]!!)
    assertFalse(analyzer.isNullable(parsers["S"]!!))
    assertFalse(analyzer.isNullable(parsers["A"]!!))
    assertFalse(analyzer.isNullable(parsers["B"]!!))
    assertFalse(analyzer.isNullable(parsers["a"]!!))
    assertFalse(analyzer.isNullable(parsers["b"]!!))
  }

  @Test
  fun test_isNullable_recursive() {
    val parsers = createRecursive()
    val analyzer = Analyzer(parsers["S"]!!)
    assertFalse(analyzer.isNullable(parsers["S"]!!))
    assertFalse(analyzer.isNullable(parsers["P"]!!))
    assertFalse(analyzer.isNullable(parsers["p"]!!))
  }

  @Test
  fun test_isNullable_selfReference() {
    val parser = createSelfReference()
    val analyzer = Analyzer(parser)
    assertFalse(analyzer.isNullable(parser))
  }

  @Test
  fun test_firstSet() {
    val a = char('a')
    val b = char('b')
    val plus = a.plus()
    expectTerminals(Analyzer(plus).firstSet(plus), listOf("a"))
    val star = a.star()
    expectTerminals(Analyzer(star).firstSet(star), listOf("a", ""))
    val opt = a.optional()
    expectTerminals(Analyzer(opt).firstSet(opt), listOf("a", ""))
    val choice = a or b
    expectTerminals(Analyzer(choice).firstSet(choice), listOf("a", "b"))
    val epsChoice = a or epsilon()
    expectTerminals(Analyzer(epsChoice).firstSet(epsChoice), listOf("a", ""))
    val seq = a.seq(b)
    expectTerminals(Analyzer(seq).firstSet(seq), listOf("a"))
    val epsSeq = epsilon().seq(a)
    expectTerminals(Analyzer(epsSeq).firstSet(epsSeq), listOf("a"))
    val optSeq = a.optional().seq(b)
    expectTerminals(Analyzer(optSeq).firstSet(optSeq), listOf("a", "b"))
  }

  @Test
  fun test_firstSet_uebersetzerbau() {
    val parsers = createUebersetzerbau()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.firstSet(parsers["S"]!!), listOf("a", "b", "c"))
    expectTerminals(analyzer.firstSet(parsers["A"]!!), listOf("a", "b", ""))
    expectTerminals(analyzer.firstSet(parsers["B"]!!), listOf("b", ""))
    expectTerminals(analyzer.firstSet(parsers["a"]!!), listOf("a"))
    expectTerminals(analyzer.firstSet(parsers["b"]!!), listOf("b"))
    expectTerminals(analyzer.firstSet(parsers["c"]!!), listOf("c"))
    expectTerminals(analyzer.firstSet(parsers["d"]!!), listOf("d"))
    expectTerminals(analyzer.firstSet(parsers["e"]!!), listOf(""))
  }

  @Test
  fun test_firstSet_dragon() {
    val parsers = createDragon()
    val analyzer = Analyzer(parsers["E"]!!)
    expectTerminals(analyzer.firstSet(parsers["E"]!!), listOf("(", "i"))
    expectTerminals(analyzer.firstSet(parsers["Ep"]!!), listOf("+", ""))
    expectTerminals(analyzer.firstSet(parsers["T"]!!), listOf("(", "i"))
    expectTerminals(analyzer.firstSet(parsers["Tp"]!!), listOf("*", ""))
    expectTerminals(analyzer.firstSet(parsers["F"]!!), listOf("(", "i"))
  }

  @Test
  fun test_firstSet_ambiguous() {
    val parsers = createAmbiguous()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.firstSet(parsers["S"]!!), listOf("a", "b"))
    expectTerminals(analyzer.firstSet(parsers["A"]!!), listOf("a", "b"))
    expectTerminals(analyzer.firstSet(parsers["B"]!!), listOf("a"))
    expectTerminals(analyzer.firstSet(parsers["a"]!!), listOf("a"))
    expectTerminals(analyzer.firstSet(parsers["b"]!!), listOf("b"))
  }

  @Test
  fun test_firstSet_recursive() {
    val parsers = createRecursive()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.firstSet(parsers["S"]!!), listOf("p"))
    expectTerminals(analyzer.firstSet(parsers["P"]!!), listOf("p"))
    expectTerminals(analyzer.firstSet(parsers["p"]!!), listOf("p"))
  }

  @Test
  fun test_firstSet_selfReference() {
    val parser = createSelfReference()
    val analyzer = Analyzer(parser)
    expectTerminals(analyzer.firstSet(parser), emptyList())
  }

  @Test
  fun test_followSet() {
    val a = char('a')
    val b = char('b')
    val plus = a.plus()
    expectTerminals(Analyzer(plus).followSet(plus), listOf(""))
    expectTerminals(Analyzer(plus).followSet(plus.children[0]), listOf("a", ""))

    val star = a.star()
    expectTerminals(Analyzer(star).followSet(star), listOf(""))
    expectTerminals(Analyzer(star).followSet(star.children[0]), listOf("a", ""))

    val opt = a.optional()
    expectTerminals(Analyzer(opt).followSet(opt), listOf(""))
    expectTerminals(Analyzer(opt).followSet(opt.children[0]), listOf(""))

    val choice = a or b
    expectTerminals(Analyzer(choice).followSet(choice), listOf(""))
    expectTerminals(Analyzer(choice).followSet(choice.children[0]), listOf(""))
    expectTerminals(Analyzer(choice).followSet(choice.children[1]), listOf(""))

    val seq = a.seq(b)
    expectTerminals(Analyzer(seq).followSet(seq), listOf(""))
    expectTerminals(Analyzer(seq).followSet(seq.children[0]), listOf("b"))
    expectTerminals(Analyzer(seq).followSet(seq.children[1]), listOf(""))

    val optSeq = a.seq(b.optional())
    expectTerminals(Analyzer(optSeq).followSet(optSeq), listOf(""))
    expectTerminals(Analyzer(optSeq).followSet(optSeq.children[0]), listOf("b", ""))
    expectTerminals(Analyzer(optSeq).followSet(optSeq.children[1]), listOf(""))
  }

  @Test
  fun test_followSet_uebersetzerbau() {
    val parsers = createUebersetzerbau()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.followSet(parsers["S"]!!), listOf(""))
    expectTerminals(analyzer.followSet(parsers["A"]!!), listOf("b", "c"))
    expectTerminals(analyzer.followSet(parsers["B"]!!), listOf("b", "c"))
    expectTerminals(analyzer.followSet(parsers["a"]!!), listOf("b", "c"))
    expectTerminals(analyzer.followSet(parsers["b"]!!), listOf("b", "c"))
    expectTerminals(analyzer.followSet(parsers["c"]!!), listOf("d"))
    expectTerminals(analyzer.followSet(parsers["d"]!!), listOf(""))
    expectTerminals(analyzer.followSet(parsers["e"]!!), listOf("b", "c"))
  }

  @Test
  fun test_followSet_dragon() {
    val parsers = createDragon()
    val analyzer = Analyzer(parsers["E"]!!)
    expectTerminals(analyzer.followSet(parsers["E"]!!), listOf(")", ""))
    expectTerminals(analyzer.followSet(parsers["Ep"]!!), listOf(")", ""))
    expectTerminals(analyzer.followSet(parsers["T"]!!), listOf(")", "+", ""))
    expectTerminals(analyzer.followSet(parsers["Tp"]!!), listOf(")", "+", ""))
    expectTerminals(analyzer.followSet(parsers["F"]!!), listOf(")", "+", "*", ""))
  }

  @Test
  fun test_followSet_ambiguous() {
    val parsers = createAmbiguous()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.followSet(parsers["S"]!!), listOf("a", ""))
    expectTerminals(analyzer.followSet(parsers["A"]!!), listOf("a", "b", ""))
    expectTerminals(analyzer.followSet(parsers["B"]!!), listOf("a", "b", ""))
    expectTerminals(analyzer.followSet(parsers["a"]!!), listOf("a", "b", ""))
    expectTerminals(analyzer.followSet(parsers["b"]!!), listOf("a", "b", ""))
  }

  @Test
  fun test_followSet_recursive() {
    val parsers = createRecursive()
    val analyzer = Analyzer(parsers["S"]!!)
    expectTerminals(analyzer.followSet(parsers["S"]!!), listOf("+", ""))
    expectTerminals(analyzer.followSet(parsers["P"]!!), listOf("+", ""))
    expectTerminals(analyzer.followSet(parsers["p"]!!), listOf("+", ""))
  }

  @Test
  fun test_followSet_selfReference() {
    val parser = createSelfReference()
    val analyzer = Analyzer(parser)
    expectTerminals(analyzer.followSet(parser), listOf(""))
  }

  @Test
  fun test_cycleSet() {
    val uebersetzerbau = createUebersetzerbau()
    val a1 = Analyzer(uebersetzerbau["S"]!!)
    for (p in uebersetzerbau.values) {
      assertTrue(a1.cycleSet(p).isEmpty())
    }

    val dragon = createDragon()
    val a2 = Analyzer(dragon["E"]!!)
    for (p in dragon.values) {
      assertTrue(a2.cycleSet(p).isEmpty())
    }

    val ambiguous = createAmbiguous()
    val a3 = Analyzer(ambiguous["S"]!!)
    assertEquals(6, a3.cycleSet(ambiguous["S"]!!).size)
    assertTrue(a3.cycleSet(ambiguous["S"]!!).containsAll(listOf(ambiguous["S"]!!, ambiguous["A"]!!)))
    assertEquals(6, a3.cycleSet(ambiguous["A"]!!).size)
    assertTrue(a3.cycleSet(ambiguous["A"]!!).containsAll(listOf(ambiguous["S"]!!, ambiguous["A"]!!)))
    assertEquals(3, a3.cycleSet(ambiguous["B"]!!).size)
    assertTrue(a3.cycleSet(ambiguous["B"]!!).contains(ambiguous["B"]!!))
    assertTrue(a3.cycleSet(ambiguous["a"]!!).isEmpty())
    assertTrue(a3.cycleSet(ambiguous["b"]!!).isEmpty())

    val recursive = createRecursive()
    val a4 = Analyzer(recursive["S"]!!)
    assertEquals(4, a4.cycleSet(recursive["S"]!!).size)
    assertTrue(a4.cycleSet(recursive["S"]!!).containsAll(listOf(recursive["S"]!!, recursive["P"]!!)))
    assertEquals(4, a4.cycleSet(recursive["P"]!!).size)
    assertTrue(a4.cycleSet(recursive["P"]!!).containsAll(listOf(recursive["S"]!!, recursive["P"]!!)))
    assertTrue(a4.cycleSet(recursive["p"]!!).isEmpty())

    val self = createSelfReference()
    val a5 = Analyzer(self)
    assertEquals(listOf(self), a5.cycleSet(self))
  }

  @Test
  fun test_parser_path_edge_cases() {
    val p = char('a')
    val path = ParserPath(listOf(p), emptyList())
    assertTrue(path.toString().contains("ParserPath"))

    assertFailsWith<IllegalArgumentException> {
      ParserPath(emptyList(), emptyList())
    }
    assertFailsWith<IllegalArgumentException> {
      ParserPath(listOf(p), listOf(0))
    }
    assertFailsWith<IllegalArgumentException> {
      val s = seq(char('a'), char('b'))
      ParserPath(listOf(s, char('c')), listOf(0))
    }
  }

  @Test
  fun test_first_set_all_nullable() {
    val s = seq(char('a').optional(), char('b').optional())
    val analyzer = Analyzer(s)
    val firsts = analyzer.firstSet(s)
    assertTrue(firsts.contains(Analyzer.sentinel))
  }

  @Test
  fun test_analyzer_unregistered_parser_failures() {
    val root = char('a')
    val foreign = char('b')
    val analyzer = Analyzer(root)

    val e1 = assertFailsWith<IllegalArgumentException> {
      analyzer.allChildren(foreign)
    }
    assertEquals("parser is not part of the analyzer", e1.message)

    val e2 = assertFailsWith<IllegalArgumentException> {
      analyzer.findPathTo(root, foreign)
    }
    assertEquals("target is not part of the analyzer", e2.message)

    val e3 = assertFailsWith<IllegalArgumentException> {
      analyzer.findAllPaths(foreign) { true }.toList()
    }
    assertEquals("source is not part of the analyzer", e3.message)

    val e4 = assertFailsWith<IllegalArgumentException> {
      analyzer.findAllPathsTo(root, foreign).toList()
    }
    assertEquals("target is not part of the analyzer", e4.message)
  }

  @Test
  fun test_is_nullable_position() {
    assertTrue(isNullable(org.petitparser.core.parser.misc.position()))
  }

  @Test
  fun test_is_sequence_edge_cases() {
    val singleSeq = org.petitparser.core.parser.combinator.SequenceParser<Char>(listOf(char('a')))
    assertFalse(isSequence(singleSeq))
    val multiSeq = org.petitparser.core.parser.combinator.SequenceParser<Char>(listOf(char('a'), char('b')))
    assertTrue(isSequence(multiSeq))
  }

  @Test
  fun test_is_parser_iterable_equal() {
    val a = listOf(char('a'))
    val b = listOf(char('b'))
    val ab = listOf(char('a'), char('b'))
    assertTrue(isParserIterableEqual(a, listOf(char('a'))))
    assertFalse(isParserIterableEqual(a, b))
    assertFalse(isParserIterableEqual(ab, a))
    assertFalse(isParserIterableEqual(a, ab))
  }
}
