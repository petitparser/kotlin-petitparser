package org.petitparser.core.expression

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.assertFailure
import org.petitparser.core.parser.assertSuccess
import org.petitparser.core.parser.combinator.optional
import org.petitparser.core.parser.combinator.seq
import org.petitparser.core.parser.consumer.any
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.consumer.noneOf
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.misc.epsilonWith
import org.petitparser.core.parser.repeater.plus
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExpressionTest {
  private fun buildParser(): Parser<Any?> {
    val builder = ExpressionBuilder<Any?>()
    builder.primitive(
      seq(
        digit().plus(),
        seq(char('.'), digit().plus()).optional(),
      ).flatten("number expected").trim(),
    )
    builder.group().apply {
      wrapper(char('(').trim(), char(')').trim()) { left, value, right -> listOf(left, value, right) }
      wrapper(string("sqrt(").trim(), char(')').trim()) { left, value, right -> listOf(left, value, right) }
    }
    builder.group().prefix(char('-').trim()) { op, a -> listOf(op, a) }
    builder.group().apply {
      postfix(string("++").trim()) { a, op -> listOf(a, op) }
      postfix(string("--").trim()) { a, op -> listOf(a, op) }
    }
    builder.group().right(char('^').trim()) { a, op, b -> listOf(a, op, b) }
    builder.group().apply {
      left(char('*').trim()) { a, op, b -> listOf(a, op, b) }
      left(char('/').trim()) { a, op, b -> listOf(a, op, b) }
    }
    builder.group().apply {
      left(char('+').trim()) { a, op, b -> listOf(a, op, b) }
      left(char('-').trim()) { a, op, b -> listOf(a, op, b) }
    }
    return builder.build().end()
  }

  private fun buildEvaluator(): Parser<Double> {
    val builder = ExpressionBuilder<Double>()
    builder.primitive(
      seq(
        digit().plus(),
        seq(char('.'), digit().plus()).optional(),
      ).flatten("number expected").trim().map(String::toDouble),
    )
    builder.group().apply {
      wrapper(char('(').trim(), char(')').trim()) { _, value, _ -> value }
      wrapper(string("sqrt(").trim(), char(')').trim()) { _, value, _ -> sqrt(value) }
    }
    builder.group().prefix(char('-').trim()) { _, a -> -a }
    builder.group().apply {
      postfix(string("++").trim()) { a, _ -> a + 1.0 }
      postfix(string("--").trim()) { a, _ -> a - 1.0 }
    }
    builder.group().right(char('^').trim()) { a, _, b -> a.pow(b) }
    builder.group().apply {
      left(char('*').trim()) { a, _, b -> a * b }
      left(char('/').trim()) { a, _, b -> a / b }
    }
    builder.group().apply {
      left(char('+').trim()) { a, _, b -> a + b }
      left(char('-').trim()) { a, _, b -> a - b }
    }
    return builder.build().end()
  }

  @Test
  fun test_add() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1 + 2", listOf("1", '+', "2"))
    assertSuccess(parser, "1 + 2 + 3", listOf(listOf("1", '+', "2"), '+', "3"))

    assertSuccess(evaluator, "1 + 2", 3.0)
    assertSuccess(evaluator, "2 + 1", 3.0)
    assertSuccess(evaluator, "1 + 2.3", 3.3)
    assertSuccess(evaluator, "2.3 + 1", 3.3)
    assertSuccess(evaluator, "1 + -2", -1.0)
    assertSuccess(evaluator, "-2 + 1", -1.0)

    assertSuccess(evaluator, "1", 1.0)
    assertSuccess(evaluator, "1 + 2", 3.0)
    assertSuccess(evaluator, "1 + 2 + 3", 6.0)
    assertSuccess(evaluator, "1 + 2 + 3 + 4", 10.0)
    assertSuccess(evaluator, "1 + 2 + 3 + 4 + 5", 15.0)

    assertFailure(evaluator, "1 +", message = "end of input expected", position = 2)
    assertFailure(evaluator, "1 + 2 +", message = "end of input expected", position = 6)
  }

  @Test
  fun test_sub() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1 - 2", listOf("1", '-', "2"))
    assertSuccess(parser, "1 - 2 - 3", listOf(listOf("1", '-', "2"), '-', "3"))

    assertSuccess(evaluator, "1 - 2", -1.0)
    assertSuccess(evaluator, "1.2 - 1.2", 0.0)
    assertSuccess(evaluator, "1 - -2", 3.0)
    assertSuccess(evaluator, "-1 - -2", 1.0)

    assertSuccess(evaluator, "1", 1.0)
    assertSuccess(evaluator, "1 - 2", -1.0)
    assertSuccess(evaluator, "1 - 2 - 3", -4.0)
    assertSuccess(evaluator, "1 - 2 - 3 - 4", -8.0)
    assertSuccess(evaluator, "1 - 2 - 3 - 4 - 5", -13.0)

    assertFailure(evaluator, "1 -", message = "end of input expected", position = 2)
    assertFailure(evaluator, "1 - 2 -", message = "end of input expected", position = 6)
  }

  @Test
  fun test_mul() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1 * 2", listOf("1", '*', "2"))
    assertSuccess(parser, "1 * 2 * 3", listOf(listOf("1", '*', "2"), '*', "3"))

    assertSuccess(evaluator, "2 * 3", 6.0)
    assertSuccess(evaluator, "2 * -4", -8.0)

    assertSuccess(evaluator, "1 * 2", 2.0)
    assertSuccess(evaluator, "1 * 2 * 3", 6.0)
    assertSuccess(evaluator, "1 * 2 * 3 * 4", 24.0)
    assertSuccess(evaluator, "1 * 2 * 3 * 4 * 5", 120.0)

    assertFailure(evaluator, "1 *", message = "end of input expected", position = 2)
    assertFailure(evaluator, "1 * 2 *", message = "end of input expected", position = 6)
  }

  @Test
  fun test_div() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1 / 2", listOf("1", '/', "2"))
    assertSuccess(parser, "1 / 2 / 3", listOf(listOf("1", '/', "2"), '/', "3"))

    assertSuccess(evaluator, "12 / 3", 4.0)
    assertSuccess(evaluator, "-16 / -4", 4.0)

    assertSuccess(evaluator, "100 / 2", 50.0)
    assertSuccess(evaluator, "100 / 2 / 2", 25.0)
    assertSuccess(evaluator, "100 / 2 / 2 / 5", 5.0)
    assertSuccess(evaluator, "100 / 2 / 2 / 5 / 5", 1.0)

    assertFailure(evaluator, "1 /", message = "end of input expected", position = 2)
    assertFailure(evaluator, "1 / 2 /", message = "end of input expected", position = 6)
  }

  @Test
  fun test_pow() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1 ^ 2", listOf("1", '^', "2"))
    assertSuccess(parser, "1 ^ 2 ^ 3", listOf("1", '^', listOf("2", '^', "3")))

    assertSuccess(evaluator, "2 ^ 3", 8.0)
    assertSuccess(evaluator, "-2 ^ 3", -8.0)
    assertSuccess(evaluator, "-2 ^ -3", -0.125)

    assertSuccess(evaluator, "4 ^ 3", 64.0)
    assertSuccess(evaluator, "4 ^ 3 ^ 2", 262144.0)
    assertSuccess(evaluator, "4 ^ 3 ^ 2 ^ 1", 262144.0)
    assertSuccess(evaluator, "4 ^ 3 ^ 2 ^ 1 ^ 0", 262144.0)

    assertFailure(evaluator, "1 ^", message = "end of input expected", position = 2)
    assertFailure(evaluator, "1 ^ 2 ^", message = "end of input expected", position = 6)
  }

  @Test
  fun test_parens() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "(1)", listOf('(', "1", ')'))
    assertSuccess(parser, "(1 + 2)", listOf('(', listOf("1", '+', "2"), ')'))
    assertSuccess(parser, "((1))", listOf('(', listOf('(', "1", ')'), ')'))
    assertSuccess(parser, "2 * (3 + 4)", listOf("2", '*', listOf('(', listOf("3", '+', "4"), ')')))
    assertSuccess(parser, "(2 + 3) * 4", listOf(listOf('(', listOf("2", '+', "3"), ')'), '*', "4"))

    assertSuccess(evaluator, "(1)", 1.0)
    assertSuccess(evaluator, "(1 + 2)", 3.0)
    assertSuccess(evaluator, "((1))", 1.0)
    assertSuccess(evaluator, "((1 + 2))", 3.0)
    assertSuccess(evaluator, "2 * (3 + 4)", 14.0)
    assertSuccess(evaluator, "(2 + 3) * 4", 20.0)
    assertSuccess(evaluator, "6 / (2 + 4)", 1.0)
    assertSuccess(evaluator, "(2 + 6) / 2", 4.0)

    assertFailure(evaluator, "(", message = "number expected")
    assertFailure(evaluator, "()", message = "number expected")
    assertFailure(evaluator, "(1", message = "number expected")
    assertFailure(evaluator, "((", message = "number expected")
  }

  @Test
  fun test_sqrt() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "sqrt(4)", listOf("sqrt(", "4", ')'))
    assertSuccess(parser, "sqrt(1 + 3)", listOf("sqrt(", listOf("1", '+', "3"), ')'))
    assertSuccess(parser, "1 + sqrt(16)", listOf("1", '+', listOf("sqrt(", "16", ')')))

    assertSuccess(evaluator, "sqrt(4)", 2.0)
    assertSuccess(evaluator, "sqrt(1 + 3)", 2.0)
    assertSuccess(evaluator, "1 + sqrt(16)", 5.0)
    assertSuccess(evaluator, "sqrt(sqrt(16))", 2.0)

    assertFailure(evaluator, "sqrt(", message = "number expected")
    assertFailure(evaluator, "sqrt()", message = "number expected")
  }

  @Test
  fun test_postfix_add() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "0++", listOf("0", "++"))
    assertSuccess(parser, "0++++", listOf(listOf("0", "++"), "++"))
    assertSuccess(parser, "0+++1", listOf(listOf("0", "++"), '+', "1"))

    assertSuccess(evaluator, "0++", 1.0)
    assertSuccess(evaluator, "0++++", 2.0)
    assertSuccess(evaluator, "0++++++", 3.0)
    assertSuccess(evaluator, "0+++1", 2.0)

    assertFailure(evaluator, "++", message = "number expected")
    assertFailure(evaluator, "0+++", message = "end of input expected", position = 3)
  }

  @Test
  fun test_postfix_sub() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "0--", listOf("0", "--"))
    assertSuccess(parser, "0----", listOf(listOf("0", "--"), "--"))
    assertSuccess(parser, "0---1", listOf(listOf("0", "--"), '-', "1"))

    assertSuccess(evaluator, "1--", 0.0)
    assertSuccess(evaluator, "2----", 0.0)
    assertSuccess(evaluator, "3------", 0.0)
    assertSuccess(evaluator, "2---1", 0.0)

    assertFailure(evaluator, "--", message = "number expected", position = 2)
    assertFailure(evaluator, "0---", message = "end of input expected", position = 3)
  }

  @Test
  fun test_negate() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "1", "1")
    assertSuccess(parser, "-1", listOf('-', "1"))
    assertSuccess(parser, "--1", listOf('-', listOf('-', "1")))
    assertSuccess(parser, "---1", listOf('-', listOf('-', listOf('-', "1"))))

    assertSuccess(evaluator, "1", 1.0)
    assertSuccess(evaluator, "-1", -1.0)
    assertSuccess(evaluator, "--1", 1.0)
    assertSuccess(evaluator, "---1", -1.0)

    assertFailure(evaluator, "-", message = "number expected", position = 1)
    assertFailure(evaluator, "--", message = "number expected", position = 2)
    assertFailure(evaluator, "+2", message = "number expected", position = 0)
  }

  @Test
  fun test_number() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "0", "0")
    assertSuccess(parser, "0.1", "0.1")
    assertSuccess(parser, "-1", listOf('-', "1"))

    assertSuccess(evaluator, "0", 0.0)
    assertSuccess(evaluator, "0.0", 0.0)
    assertSuccess(evaluator, "1", 1.0)
    assertSuccess(evaluator, "1.2", 1.2)
    assertSuccess(evaluator, "34", 34.0)
    assertSuccess(evaluator, "34.7", 34.7)
    assertSuccess(evaluator, "56.78", 56.78)

    assertFailure(evaluator, "", message = "number expected")
    assertFailure(evaluator, "-", message = "number expected", position = 1)
    assertFailure(evaluator, "(", message = "number expected")
    assertFailure(evaluator, "0.", message = "end of input expected", position = 1)
  }

  @Test
  fun test_priority() {
    val parser = buildParser()
    val evaluator = buildEvaluator()

    assertSuccess(parser, "2 * 3 + 4", listOf(listOf("2", '*', "3"), '+', "4"))
    assertSuccess(parser, "2 + 3 * 4", listOf("2", '+', listOf("3", '*', "4")))

    assertSuccess(evaluator, "2 * 3 + 4", 10.0)
    assertSuccess(evaluator, "2 + 3 * 4", 14.0)
    assertSuccess(evaluator, "6 / 3 + 4", 6.0)
    assertSuccess(evaluator, "2 + 6 / 2", 5.0)
  }

  @Test
  fun test_builder_empty() {
    val builder = ExpressionBuilder<String>()
    assertFailsWith<IllegalStateException> {
      builder.build()
    }
  }

  @Test
  fun test_builder_no_primitive() {
    val builder = ExpressionBuilder<String>()
    builder.group().wrapper(char('('), char(')')) { _, v, _ -> "[$v]" }
    assertFailsWith<IllegalStateException> {
      builder.build()
    }
  }

  @Test
  fun test_loopback() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(seq(char('a'), builder.loopback).flatten())
    builder.primitive(char('b').map(Char::toString))
    val parser = builder.build()
    assertSuccess(parser, "b", "b")
    assertSuccess(parser, "ab", "ab")
    assertSuccess(parser, "aab", "aab")
  }

  @Test
  fun test_epsilon_primitive() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(noneOf("()").map(Char::toString))
    builder.primitive(epsilonWith("*"))
    builder.group().wrapper(char('('), char(')')) { _, v, _ -> "[$v]" }
    val parser = builder.build().end()
    assertSuccess(parser, "", "*")
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "(a)", "[a]")
    assertSuccess(parser, "((a))", "[[a]]")
    assertSuccess(parser, "()", "[*]")
    assertSuccess(parser, "(())", "[[*]]")
  }

  @Test
  fun test_epsilon_left() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(any().map(Char::toString))
    builder.group().left(epsilonWith(null)) { a, _, b -> "[$a$b]" }
    val parser = builder.build().end()
    assertFailure(parser, "")
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "ab", "[ab]")
    assertSuccess(parser, "abc", "[[ab]c]")
    assertSuccess(parser, "abcd", "[[[ab]c]d]")
  }

  @Test
  fun test_epsilon_right() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(any().map(Char::toString))
    builder.group().right(epsilonWith(null)) { a, _, b -> "[$a$b]" }
    val parser = builder.build().end()
    assertFailure(parser, "")
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "ab", "[ab]")
    assertSuccess(parser, "abc", "[a[bc]]")
    assertSuccess(parser, "abcd", "[a[b[cd]]]")
  }

  @Test
  fun test_optional_basic() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(digit().map(Char::toString))
    builder.group().apply {
      wrapper(char('('), char(')')) { _, v, _ -> "($v)" }
      optional("∅")
    }
    val parser = builder.build().end()
    assertSuccess(parser, "", "∅")
    assertSuccess(parser, "()", "(∅)")
    assertSuccess(parser, "1", "1")
    assertSuccess(parser, "(1)", "(1)")
  }

  @Test
  fun test_optional_repeated() {
    val builder = ExpressionBuilder<String>()
    val group = builder.group()
    group.optional("foo")
    assertFailsWith<IllegalStateException> {
      group.optional("bar")
    }
  }

  @Test
  fun test_regex_example() {
    val builder = ExpressionBuilder<String>()
    builder.primitive(noneOf(")").map(Char::toString))
    builder.group().apply {
      wrapper(char('('), char(')')) { _, value, _ -> "($value)" }
      prefix(char('!')) { _, value -> "!($value)" }
      postfix(char('?')) { value, _ -> "($value)?" }
      left(char('|')) { left, _, right -> "($left|$right)" }
      right(char('&')) { left, _, right -> "($left&$right)" }
    }
    builder.group().apply {
      left(epsilonWith(null)) { a, _, b -> "[$a$b]" }
      optional("∅")
    }
    val parser = builder.build().end()
    assertSuccess(parser, "", "∅")
    assertSuccess(parser, "a", "a")
    assertSuccess(parser, "ab", "[ab]")
    assertSuccess(parser, "abc", "[[ab]c]")
    assertSuccess(parser, "a&b", "(a&b)")
    assertSuccess(parser, "a&b&c", "(a&(b&c))")
    assertSuccess(parser, "a|b", "(a|b)")
    assertSuccess(parser, "a|b|c", "((a|b)|c)")
    assertSuccess(parser, "a?", "(a)?")
    assertSuccess(parser, "a??", "((a)?)?")
    assertSuccess(parser, "!a", "!(a)")
    assertSuccess(parser, "!!a", "!(!(a))")
    assertSuccess(parser, "()", "(∅)")
    assertSuccess(parser, "(a)", "(a)")
    assertSuccess(parser, "(ab)", "([ab])")
    assertSuccess(parser, "(abc)", "([[ab]c])")
  }

  @Test
  fun test_buildExpression_dsl() {
    val parser = buildExpression<Int> {
      primitive(digit().map { it.digitToInt() })
      group {
        wrapper(char('('), char(')'))
      }
      group {
        prefix(char('-')) { v -> -v }
      }
      group {
        left(char('*')) { a, b -> a * b }
        left(char('/')) { a, b -> a / b }
      }
      group {
        left(char('+')) { a, b -> a + b }
        left(char('-')) { a, b -> a - b }
      }
    }.end()

    assertSuccess(parser, "1+2*3", 7)
    assertSuccess(parser, "(1+2)*3", 9)
    assertSuccess(parser, "-5+3", -2)
  }

  @Test
  fun test_expression_results() {
    val prefixResult = ExpressionResultPrefix("+") { op: String, v: Int -> v + op.length }
    assertEquals("+", prefixResult.operator)
    assertEquals(6, prefixResult(5))
    assertTrue(prefixResult.toString().contains("+"))

    val postfixResult = ExpressionResultPostfix("++") { v: Int, op: String -> v + op.length }
    assertEquals("++", postfixResult.operator)
    assertEquals(7, postfixResult(5))
    assertTrue(postfixResult.toString().contains("++"))

    val infixResult = ExpressionResultInfix("*") { a: Int, _: String, b: Int -> a * b }
    assertEquals("*", infixResult.operator)
    assertEquals(15, infixResult(3, 5))
    assertTrue(infixResult.toString().contains("*"))
  }

  @Test
  fun test_group_primitive() {
    val parser = buildExpression<String> {
      primitive(char('a').map(Char::toString))
      group {
        primitive(char('b').map(Char::toString))
        left(char('+')) { left, right -> "($left+$right)" }
      }
    }
    assertSuccess(parser, "a+b", "(a+b)")
    assertSuccess(parser, "b+a", "(b+a)")
  }

  @Test
  fun test_group_primitive_only() {
    val parser = buildExpression<String> {
      group {
        primitive(digit().map(Char::toString))
        left(char('+')) { a, b -> "($a+$b)" }
      }
    }
    assertSuccess(parser, "1+2", "(1+2)")
  }

  @Test
  fun test_multiple_group_primitives() {
    val parser = buildExpression<String> {
      group {
        primitive(char('a').map(Char::toString))
      }
      group {
        primitive(char('b').map(Char::toString))
        left(char('+')) { a, b -> "($a+$b)" }
      }
    }
    assertSuccess(parser, "a+b", "(a+b)")
    assertSuccess(parser, "b+a", "(b+a)")
  }

  @Test
  fun test_postfix_and_right_simplified_callbacks() {
    val parser = buildExpression<Int> {
      primitive(digit().map { it.digitToInt() })
      group {
        postfix(char('!')) { a -> a * 2 }
        right(char('^')) { a, b -> a + b }
      }
    }
    assertSuccess(parser, "3!", 6)
    assertSuccess(parser, "2^3^4", 9)
  }
}
