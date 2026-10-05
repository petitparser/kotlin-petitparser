package org.petitparser.core.definition

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.accept
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.or
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.combinator.seqOf
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.letter
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.consumer.word
import org.petitparser.core.parser.misc.EpsilonParser
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.misc.epsilon
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.plusSeparated
import org.petitparser.core.parser.repeater.star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

open class ListGrammarDefinition : GrammarDefinition<Any?>() {
  override fun start(): Parser<Any?> = ref(::list).end()

  open fun list(): Parser<Any?> =
    seqMap(ref(::element), char(','), ref(::list)) { first, comma, rest ->
      listOf(first, comma, rest)
    } / ref(::element)

  open fun element(): Parser<Any?> = digit().plus().flatten()
}

class ListParserDefinition : ListGrammarDefinition() {
  override fun element(): Parser<Any?> = super.element().map { (it as String).toInt() }
}

class TokenizedListGrammarDefinition : GrammarDefinition<Any?>() {
  override fun start(): Parser<Any?> = ref(::list).end()

  fun list(): Parser<Any?> =
    seqMap(ref(::element), ref(::token, char(',')), ref(::list)) { first, comma, rest ->
      listOf(first, comma, rest)
    } / ref(::element)

  fun element(): Parser<Any?> = ref(::token, digit().plus())

  fun token(parser: Parser<*>): Parser<String> = parser.flatten().trim()
}

class TypedReferencesGrammarDefinition : GrammarDefinition<List<String>>() {
  override fun start(): Parser<List<String>> = ref(::f0)

  fun f0(): Parser<List<String>> = ref1(::f1, 1)
  fun f1(a1: Int): Parser<List<String>> = ref2(::f2, a1, 2)
  fun f2(a1: Int, a2: Int): Parser<List<String>> = ref3(::f3, a1, a2, 3)
  fun f3(a1: Int, a2: Int, a3: Int): Parser<List<String>> = ref4(::f4, a1, a2, a3, 4)
  fun f4(a1: Int, a2: Int, a3: Int, a4: Int): Parser<List<String>> = ref5(::f5, a1, a2, a3, a4, 5)
  fun f5(a1: Int, a2: Int, a3: Int, a4: Int, a5: Int): Parser<List<String>> =
    ref6(::f6, a1, a2, a3, a4, a5, 6)
  fun f6(a1: Int, a2: Int, a3: Int, a4: Int, a5: Int, a6: Int): Parser<List<String>> =
    ref7(::f7, a1, a2, a3, a4, a5, a6, 7)
  fun f7(a1: Int, a2: Int, a3: Int, a4: Int, a5: Int, a6: Int, a7: Int): Parser<List<String>> =
    ref8(::f8, a1, a2, a3, a4, a5, a6, a7, 8)
  fun f8(a1: Int, a2: Int, a3: Int, a4: Int, a5: Int, a6: Int, a7: Int, a8: Int): Parser<List<String>> =
    ref9(::f9, a1, a2, a3, a4, a5, a6, a7, a8, 9)
  fun f9(
    a1: Int,
    a2: Int,
    a3: Int,
    a4: Int,
    a5: Int,
    a6: Int,
    a7: Int,
    a8: Int,
    a9: Int,
  ): Parser<List<String>> = seqOf(
    string(a1.toString()),
    string(a2.toString()),
    string(a3.toString()),
    string(a4.toString()),
    string(a5.toString()),
    string(a6.toString()),
    string(a7.toString()),
    string(a8.toString()),
    string(a9.toString()),
  )
}

class BuggedGrammarDefinition : GrammarDefinition<Any?>() {
  override fun start(): Parser<Any?> = epsilon()

  fun directRecursion1(): Parser<Any?> = ref(::directRecursion1)

  fun indirectRecursion1(): Parser<Any?> = ref(::indirectRecursion2)
  fun indirectRecursion2(): Parser<Any?> = ref(::indirectRecursion3)
  fun indirectRecursion3(): Parser<Any?> = ref(::indirectRecursion1)

  fun delegation1(): Parser<Any?> = ref(::delegation2)
  fun delegation2(): Parser<Any?> = ref(::delegation3)
  fun delegation3(): Parser<Any?> = epsilon()
}

class LambdaGrammarDefinition : GrammarDefinition<Any?>() {
  override fun start(): Parser<Any?> = ref(::expression).end()

  fun expression(): Parser<Any?> = ref(::variable) / ref(::abstraction) / ref(::application)

  fun variable(): Parser<String> = seq(letter(), word().star()).flatten().trim()

  fun abstraction(): Parser<Any?> =
    seq(token('\\'), ref(::variable), token('.'), ref(::expression))

  fun application(): Parser<Any?> =
    seq(token('('), ref(::expression), ref(::expression), token(')'))

  fun token(value: Char): Parser<Char> = char(value).trim()
}

class ExpressionGrammarDefinition : GrammarDefinition<Any?>() {
  override fun start(): Parser<Any?> = ref(::terms).end()

  fun terms(): Parser<Any?> = ref(::addition) / ref(::factors)

  fun addition(): Parser<Any?> =
    ref(::factors).plusSeparated(token(char('+') / char('-')))

  fun factors(): Parser<Any?> = ref(::multiplication) / ref(::power)

  fun multiplication(): Parser<Any?> =
    ref(::power).plusSeparated(token(char('*') / char('/')))

  fun power(): Parser<Any?> = ref(::primary).plusSeparated(char('^').trim())

  fun primary(): Parser<Any?> = ref(::number) / ref(::parentheses)

  fun number(): Parser<Any?> = token(
    seq(
      char('-').optional(),
      digit().plus(),
      seq(char('.'), digit().plus()).optional(),
    ),
  )

  fun parentheses(): Parser<Any?> = seq(token('('), ref(::terms), token(')'))

  fun token(value: Any?): Parser<Any?> {
    return when (value) {
      is Char -> char(value).trim() as Parser<Any?>
      is String -> string(value).trim() as Parser<Any?>
      is Parser<*> -> value.flatten().trim() as Parser<Any?>
      else -> throw IllegalArgumentException("unable to parse: $value")
    }
  }
}

class PropertyGrammarDefinition : GrammarDefinition<List<String>>() {
  val element by def { digit().plus().flatten() }
  val list: Parser<List<String>> by rule { ref(::element).plusSeparated(char(',')).map { it.elements } }
  val start by def { ref(::list).end() }
}

class DefinitionTest {
  @Test
  fun test_reference_without_parameters() {
    fun number() = digit().map { it.digitToInt() }
    val ref1 = ref(::number)
    val ref2 = ref(::number)
    assertNotSame(ref1, ref2)
    assertEquals(ref1, ref2)
    assertEquals(ref1.hashCode(), ref2.hashCode())
  }

  @Test
  fun test_reference_with_different_production() {
    fun number() = digit()
    fun token() = digit().plus()
    val ref1 = ref(::number)
    val ref2 = ref(::token)
    assertTrue(ref1 !== ref2)
    assertFalse(ref1 == ref2)
  }

  @Test
  fun test_reference_with_same_parameters() {
    fun numberList(separator: Char) = digit().plusSeparated(char(separator))
    val ref1 = ref(::numberList, ',')
    val ref2 = ref(::numberList, ',')
    assertNotSame(ref1, ref2)
    assertEquals(ref1, ref2)
    assertEquals(ref1.hashCode(), ref2.hashCode())
  }

  @Test
  fun test_reference_with_different_parameters() {
    fun numberList(separator: Char) = digit().plusSeparated(char(separator))
    val ref1 = ref(::numberList, ',')
    val ref2 = ref(::numberList, ';')
    assertNotSame(ref1, ref2)
    assertFalse(ref1 == ref2)
  }

  @Test
  fun test_reference_copy_and_unsupported_methods() {
    fun number() = digit()
    val reference = ref(::number)
    val copy = reference.copy()
    assertEquals(reference, copy)
    assertTrue(reference.isEqualTo(copy))
    assertFailsWith<UnsupportedOperationException> { reference.parse("0") }
    assertFailsWith<UnsupportedOperationException> { reference.fastParseOn("0", 0) }
  }

  @Test
  fun test_typed_references_9_arguments() {
    val definition = TypedReferencesGrammarDefinition()
    val parser = definition.build()
    assertSuccess(parser, "123456789", listOf("1", "2", "3", "4", "5", "6", "7", "8", "9"))
  }

  @Test
  fun test_list_grammar() {
    val definition = ListGrammarDefinition()
    val parser = definition.build()
    assertSuccess(parser, "1,2", listOf("1", ',', "2"))
    assertSuccess(parser, "1,2,3", listOf("1", ',', listOf("2", ',', "3")))
  }

  @Test
  fun test_list_parser() {
    val definition = ListParserDefinition()
    val parser = definition.build()
    assertSuccess(parser, "1,2", listOf(1, ',', 2))
    assertSuccess(parser, "1,2,3", listOf(1, ',', listOf(2, ',', 3)))
  }

  @Test
  fun test_tokenized_list_grammar() {
    val definition = TokenizedListGrammarDefinition()
    val parser = definition.build()
    assertSuccess(parser, "1, 2", listOf("1", ",", "2"))
    assertSuccess(parser, "1,  2, 3", listOf("1", ",", listOf("2", ",", "3")))
  }

  @Test
  fun test_direct_recursion() {
    val definition = BuggedGrammarDefinition()
    assertFailsWith<IllegalStateException> {
      definition.buildFrom(definition.directRecursion1())
    }
  }

  @Test
  fun test_indirect_recursion() {
    val definition = BuggedGrammarDefinition()
    assertFailsWith<IllegalStateException> {
      definition.buildFrom(definition.indirectRecursion1())
    }
    assertFailsWith<IllegalStateException> {
      definition.buildFrom(definition.indirectRecursion2())
    }
    assertFailsWith<IllegalStateException> {
      definition.buildFrom(definition.indirectRecursion3())
    }
  }

  @Test
  fun test_delegation() {
    val definition = BuggedGrammarDefinition()
    assertTrue(definition.buildFrom(definition.delegation1()) is EpsilonParser<*>)
    assertTrue(definition.buildFrom(definition.delegation2()) is EpsilonParser<*>)
    assertTrue(definition.buildFrom(definition.delegation3()) is EpsilonParser<*>)
  }

  @Test
  fun test_lambda_example() {
    val definition = LambdaGrammarDefinition()
    val parser = definition.build()
    assertSuccess(parser, "x", "x")
    assertSuccess(parser, "xy", "xy")
    assertSuccess(parser, "x12", "x12")
    assertTrue(parser.accept("\\x.y"))
    assertTrue(parser.accept("\\x.\\y.z"))
    assertTrue(parser.accept("(x x)"))
    assertTrue(parser.accept("(x y)"))
    assertTrue(parser.accept("(x (y z))"))
    assertTrue(parser.accept("((x y) z)"))
  }

  @Test
  fun test_expression_example() {
    val definition = ExpressionGrammarDefinition()
    val parser = definition.build()
    assertTrue(parser.accept("1"))
    assertTrue(parser.accept("12"))
    assertTrue(parser.accept("1.23"))
    assertTrue(parser.accept("-12.3"))
    assertTrue(parser.accept("1 + 2"))
    assertTrue(parser.accept("1 + 2 + 3"))
    assertTrue(parser.accept("1 - 2"))
    assertTrue(parser.accept("1 - 2 - 3"))
    assertTrue(parser.accept("1 * 2"))
    assertTrue(parser.accept("1 * 2 * 3"))
    assertTrue(parser.accept("1 / 2"))
    assertTrue(parser.accept("1 / 2 / 3"))
    assertTrue(parser.accept("1 ^ 2"))
    assertTrue(parser.accept("1 ^ 2 ^ 3"))
    assertTrue(parser.accept("1 + (2 * 3)"))
    assertTrue(parser.accept("(1 + 2) * 3"))
  }

  @Test
  fun test_property_delegation() {
    val grammar = PropertyGrammarDefinition()
    assertEquals(setOf("element", "list", "start"), grammar.productionNames)
    assertEquals(grammar["element"], grammar.element)

    val parser = grammar.build()
    assertSuccess(parser, "1,2,3", listOf("1", "2", "3"))

    // Build from specific production by name
    val elementParser = grammar.buildFrom<String>("element")
    assertSuccess(elementParser, "42", "42")
    assertFailure(elementParser, "abc", "digit expected", 0)

    // Build from unknown production throws
    assertFailsWith<IllegalArgumentException> {
      grammar.buildFrom<String>("unknown")
    }
  }

  @Test
  fun test_production_delegate_standalone() {
    val delegate = def { char('a') }
    val d by delegate
    assertEquals("d", delegate.name)
    assertSuccess(d, "a", 'a')
    assertEquals(delegate, delegate)
    assertTrue(delegate.isEqualTo(delegate))
    assertEquals(delegate.copy(), delegate)
    assertTrue(delegate.toString().contains("d"))
  }

  @Test
  fun test_grammar_definition_parser_delegate() {
    class DirectParserGrammar : GrammarDefinition<Char>() {
      val charA by char('a')
      override fun start() = ref(::charA)
    }

    val grammar = DirectParserGrammar()
    assertEquals(setOf("charA"), grammar.productionNames)
    val parser = grammar.build()
    assertSuccess(parser, "a", 'a')
  }

  @Test
  fun test_reference_parser_equality_deep() {
    fun p1(p: Parser<*>) = p
    val r1 = ref(::p1, char('a'))
    val r2 = ref(::p1, char('a'))
    val r3 = ref(::p1, char('b'))
    assertEquals(r1, r2)
    assertFalse(r1 == r3)
    assertFalse(r1.equals("other"))
    assertTrue(r1 == r1)
    assertTrue(r1.isEqualTo(r2))

    fun p0() = char('a')
    val r0 = ref(::p0)
    assertFalse(r0 == r1)
  }

  @Test
  fun test_production_delegate_equality_and_unnamed() {
    val d1 = def { char('a') }
    val d2 = def { char('a') }
    assertEquals("ProductionDelegate", d1.toString())
    assertFalse(d1.equals("other"))
    assertEquals(d1, d1)

    class Dummy : GrammarDefinition<Char>() {
      val a by d1
      val b by d2
    }
    Dummy()
    assertFalse(d1 == d2)

    class Dummy1 : GrammarDefinition<Char>() {
      val a by def { char('a') }
    }
    class Dummy2 : GrammarDefinition<Char>() {
      val a by def { char('b') }
    }
    val g1 = Dummy1()
    val g2 = Dummy2()
    assertFalse(g1.a == g2.a)
  }

  @Test
  fun test_grammar_definition_method_start() {
    val z = char('z')
    class MethodGrammar : GrammarDefinition<Char>() {
      override fun start(): Parser<Char> = z
    }
    val grammar = MethodGrammar()
    assertEquals(z, grammar["start"])
    val parser = grammar.build()
    assertSuccess(parser, "z", 'z')
    val parserByName = grammar.buildFrom<Char>("start")
    assertSuccess(parserByName, "z", 'z')
  }

  @Test
  fun test_resolve_chained_references_memoization() {
    val leaf = char('x')
    val r3 = ref { leaf }
    val r2 = ref { r3 }
    val r1 = ref { r2 }
    val pair = seqOf(r1, r2)
    val resolved = resolve(pair)
    assertSuccess(resolved, "xx", listOf('x', 'x'))
  }

  @Test
  fun test_grammar_definition_missing_start() {
    class EmptyGrammar : GrammarDefinition<String>()
    val grammar = EmptyGrammar()
    assertFailsWith<UnsupportedOperationException> {
      grammar.start()
    }
  }
}
