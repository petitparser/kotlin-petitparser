# PetitParser for Kotlin

[![Release Status](https://jitpack.io/v/petitparser/kotlin-petitparser.svg)](https://jitpack.io/#petitparser/kotlin-petitparser)
[![Kotlin CI](https://github.com/petitparser/kotlin-petitparser/actions/workflows/gradle.yml/badge.svg)](https://github.com/petitparser/kotlin-petitparser/actions/workflows/gradle.yml)
[![GitHub Issues](https://img.shields.io/github/issues/petitparser/kotlin-petitparser.svg)](https://github.com/petitparser/kotlin-petitparser/issues)
[![GitHub Forks](https://img.shields.io/github/forks/petitparser/kotlin-petitparser.svg)](https://github.com/petitparser/kotlin-petitparser/network)
[![GitHub Stars](https://img.shields.io/github/stars/petitparser/kotlin-petitparser.svg)](https://github.com/petitparser/kotlin-petitparser/stargazers)
[![GitHub License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/petitparser/kotlin-petitparser/main/LICENSE)

Grammars for programming languages are traditionally specified statically. They are hard to compose and reuse due to ambiguities that inevitably arise. PetitParser combines ideas from [scannerless parsing](https://en.wikipedia.org/wiki/Scannerless_parsing), [parser combinators](https://en.wikipedia.org/wiki/Parser_combinator), [parsing expression grammars](https://en.wikipedia.org/wiki/Parsing_expression_grammar) (PEG), and packrat parsers to model grammars and parsers as objects that can be dynamically configured, transformed, and inspected.

PetitParser Kotlin is a Kotlin Multiplatform library supporting JVM, JS (Node.js & Browser), and Native (macOS, Linux, Windows).

---

## Table of Contents

- [Getting Started](#getting-started)
- [Writing a Simple Grammar](#writing-a-simple-grammar)
  - [Terminal Parsers](#terminal-parsers)
  - [Combinators](#combinators)
  - [Action Parsers](#action-parsers)
- [Parsing Input](#parsing-input)
- [Defining Grammars (`GrammarDefinition`)](#defining-grammars-grammardefinition)
- [Expression Builder](#expression-builder)
- [Indentation-Sensitive Parsing](#indentation-sensitive-parsing)
- [Inspection, Optimization & Linting](#inspection-optimization--linting)
- [Examples](#examples)
- [License](#license)

---

## Getting Started

Add the dependency to your `build.gradle.kts`:

```kotlin
repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.petitparser:kotlin-petitparser:1.2.0")
}
```

---

## Writing a Simple Grammar

### Terminal Parsers

Terminal parsers consume characters directly from the input:

```kotlin
import org.petitparser.core.parser.consumer.*

val charA = char('a')                // matches 'a'
val strHello = string("hello")       // matches "hello"
val anyChar = any()                  // matches any single character
val digitChar = digit()              // matches '0'..'9'
val letterChar = letter()            // matches 'a'..'z', 'A'..'Z'
val wordChar = word()                // matches letters, digits, and '_'
val spaceChar = whitespace()         // matches whitespace (' ', '\t', '\n', '\r')
val hexDigit = pattern("0-9a-fA-F")  // regex-like character pattern
```

### Combinators

Combinators assemble smaller parsers into larger structures:

#### Sequence

Combine parsers sequentially with `+`, `seq`, or `seq(...)`:

```kotlin
import org.petitparser.core.parser.combinator.*

// Returns a SequenceParser yielding a List<Any?>:
val seqList = char('a') + char('b')
val seqInfix = char('a') seq char('b')

// Type-safe sequences returning strongly typed tuples (Tuple2..Tuple9):
val typedSeq = seq(letter(), digit()) // returns Parser<Tuple2<Char, Char>>

// Strongly typed sequence mapping:
val mapped = seqMap(letter(), digit()) { l, d -> "$l$d" }
```

#### Choice

Try alternate parsers in ordered sequence with `/` or `or`:

```kotlin
val idOrNumber = letter() / digit()
val choice = char('a') or char('b')
```

#### Repetition

Repeat parsers zero or more, or one or more times:

```kotlin
import org.petitparser.core.parser.repeater.*

val zeroOrMore = digit().star()                     // List<Char>
val oneOrMore = digit().plus()                      // List<Char>
val fixedTimes = digit().times(4)                   // exactly 4 digits
val ranged = digit().repeat(2, 5)                   // 2 to 5 digits
val commaSeparated = digit().plusSeparated(char(',')) // SeparatedList
```

#### Optional and Delimiters

```kotlin
val optionalSign = char('-').optional() // nullable result

// Skipping delimiters:
val quoted = letter().surroundedBy(char('"'))  // discards quotes, returns letter
val braced = digit().surroundedBy(char('{'), char('}'))
val item = digit().followedBy(char(';'))
```

### Action Parsers

Transform, flatten, or filter successful results:

```kotlin
import org.petitparser.core.parser.action.*

// Transform results:
val integer = digit().plus().flatten().map(String::toInt)

// Tokenize with precise buffer coordinates:
val tokenized = integer.token() // produces Token<Int> with start, stop, line, column

// Trim surrounding whitespace:
val trimmed = integer.trim()

// Filter results with predicates and optional custom errors:
val evenNumber = integer.filter({ it % 2 == 0 }, message = "even number expected")
val smallNumber = integer.filter("number must be under 100") { it < 100 }
```

---

## Parsing Input

Parsers support multiple evaluation modes:

```kotlin
import org.petitparser.core.context.Output
import org.petitparser.core.parser.parse
import org.petitparser.core.parser.accept
import org.petitparser.core.parser.matches

val parser = digit().plus().flatten().map(String::toInt)

// Full parse producing typed Output:
when (val result = parser.parse("42")) {
  is Output.Success -> println("Parsed: ${result.value} at position ${result.position}")
  is Output.Failure -> println("Error at ${result.line}:${result.column}: ${result.message}")
}

// Fast check (allocation-free):
val isValid = parser.accept("42") // true

// Stream all occurrences across a buffer:
val matches = digit().plus().flatten().matches("abc 12 def 34")
// matches yields Sequence<String> ("12", "34")
```

---

## Defining Grammars (`GrammarDefinition`)

For complex, recursive, or mutually dependent grammars, extend `GrammarDefinition` and use idiomatic property delegation (`by def { ... }`):

```kotlin
import org.petitparser.core.definition.GrammarDefinition
import org.petitparser.core.definition.ref
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.plus
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.plus
import org.petitparser.core.parser.repeater.starSeparated

class ExpressionGrammar : GrammarDefinition<Double>() {
  val number: Parser<Double> by def {
    digit().plus().flatten().map(String::toDouble)
  }

  val terms: Parser<Double> by def {
    ref(::addition) / ref(::factors)
  }

  val addition: Parser<Double> by def {
    (ref(::factors) + (char('+') / char('-')) + ref(::terms)).map { (left, op, right) ->
      if (op == '+') (left as Double) + (right as Double) else (left as Double) - (right as Double)
    }
  }

  val factors: Parser<Double> by def {
    ref(::number)
  }

  val start: Parser<Double> by def { ref(::terms).end() }
}

val grammar = ExpressionGrammar()
val parser = grammar.build()
println(parser.parse("1+2+3").value) // 6.0

// Build individual sub-productions by name:
val numberParser = grammar.buildFrom<Double>("number")
```

---

## Expression Builder

`buildExpression` simplifies constructing operator-precedence parsers:

```kotlin
import org.petitparser.core.expression.buildExpression
import org.petitparser.core.parser.action.flatten
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.action.trim
import org.petitparser.core.parser.combinator.surroundedBy
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.digit
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.plus

val calculator = buildExpression<Double> {
  // Primitive terms:
  primitive(digit().plus().flatten().map(String::toDouble).trim())

  // Parentheses:
  group {
    wrapper(char('(').trim(), char(')').trim()) { _, inner, _ -> inner }
  }

  // Prefix negation:
  group {
    prefix(char('-').trim()) { _, value -> -value }
  }

  // Multiplication and division (higher precedence):
  group {
    left(char('*').trim()) { a, _, b -> a * b }
    left(char('/').trim()) { a, _, b -> a / b }
  }

  // Addition and subtraction (lower precedence):
  group {
    left(char('+').trim()) { a, _, b -> a + b }
    left(char('-').trim()) { a, _, b -> a - b }
  }
}.end()

val result = calculator.parse("2 + 3 * (4 - 1)").value // 11.0
```

---

## Indentation-Sensitive Parsing

Parse languages with indentation blocks (such as Python or YAML) using `Indent`:

```kotlin
import org.petitparser.core.indent.Indent

val indent = Indent()

// `indent.during(rule)` automatically increases indentation, runs the rule,
// and guarantees state rollback if parsing fails.
val block = indent.during(childRules)
```

---

## Inspection, Optimization & Linting

PetitParser models parsers as an inspectable AST graph:

```kotlin
import org.petitparser.core.reflection.linter
import org.petitparser.core.reflection.optimize
import org.petitparser.core.reflection.transformParser

// Detect dead code, unreferenced rules, or redundant delegates:
val issues = linter(parser)
for (issue in issues) {
  println("${issue.title}: ${issue.description}")
}

// Automatically optimize character repeaters, flatten nested choices, and eliminate delegates:
val optimized = optimize(parser)
```

---

## Examples

The library comes with full reference implementations of:

- [CSV Grammar](https://github.com/petitparser/kotlin-petitparser/blob/main/src/commonMain/kotlin/org/petitparser/grammars/Csv.kt): RFC 4180 compliant CSV parser with quoted multiline field support.
- [JSON Grammar](https://github.com/petitparser/kotlin-petitparser/blob/main/src/commonMain/kotlin/org/petitparser/grammars/Json.kt): RFC 8259 compliant JSON parser parsing full JSON object hierarchies, numbers, strings, escape sequences, and unicode codepoints.

---

## License

The MIT License, see [LICENSE](https://raw.githubusercontent.com/petitparser/kotlin-petitparser/main/LICENSE).
