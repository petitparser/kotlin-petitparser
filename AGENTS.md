# PetitParser Kotlin - Agent & Engineering Guidelines

This document outlines the architectural standards, code quality requirements, testing protocols, and style guidelines for developers and autonomous agents working on PetitParser Kotlin.

---

## 1. Project Philosophy & Kotlin-Native Design

PetitParser Kotlin is a multiplatform dynamic parser combinator framework adapted from the Smalltalk and Dart reference implementations. It is **not** a verbatim line-by-line port: all features must feel native, idiomatic, backward-compatible, and highly efficient for Kotlin.

### 1.1 Core Invariants & Backward Compatibility

1. **Backward-Compatible Functional Interface (`fun interface Parser<out R>`)**:
   - `Parser<out R>` remains a Kotlin `fun interface` with a single abstract method: `parseOn(input: Input): Output<R>`.
   - Existing code relying on SAM conversions or `Parser { input -> ... }` retains 100% source and binary compatibility.
   - AST methods (`children`, `replace`, `copy`, `isEqualTo`, `hasEqualProperties`) and performance methods (`fastParseOn`) are implemented as **interface default methods** on `Parser<out R>`.
   - Framework combinators are concrete classes inheriting `DelegateParser`, `ListParser`, or implementing `Parser` with dedicated overrides.

2. **Dual Parsing Modes**:
   - **Full Parse (`parseOn`)**: Constructs typed `Output<R>` (`Success` with parsed value, or `Failure` with message and position).
   - **Fast Parse (`fastParseOn`)**: Allocation-free parse execution returning the ending position integer (or `-1` on failure). Essential for high throughput in repeaters, lookaheads, choices, and flattening. Every concrete parser class must override `fastParseOn` whenever possible.

3. **First-Class AST Nodes**:
   - Parsers are first-class nodes in a mutable directed graph during construction and analysis.
   - Default implementations on `Parser`:
     - `copy()`: Defaults to `this` (for immutable/leaf parsers) or cloned instance (for composite parsers).
     - `children`: Defaults to `emptyList()`.
     - `replace(source, target)`: Defaults to no-op.
     - `isEqualTo(other, seen)`: Structural equivalence, correctly handling recursive cycles via `seen`.

4. **Multiplatform Purity (`commonMain`)**:
   - Code under `src/commonMain/kotlin` must run across JVM, JS (Node & Browser), and Native (macOS, Linux, Windows).
   - Never import JVM-specific APIs (`java.*`) or JS-specific APIs (`kotlin.js.*`) in `commonMain`. Use Kotlin standard library primitives and multiplatform functions.

---

## 2. Kotlin Idioms & Best Practices

1. **Operator Overloads**:
   - Choice: `parser1 / parser2` (`div`) or `parser1 or parser2`.
   - Sequence: `parser1 + parser2` (`plus`) or `parser1 seq parser2`.
   - Repetition: `parser.times(count)`.
2. **Type-Safe Sequences & Destructuring**:
   - Typed tuples `Tuple2`..`Tuple9` support Kotlin component destructuring: `seq(p1, p2).map { (a, b) -> ... }`.
   - Strongly typed `seqMap(p1, p2) { a, b -> ... }`.
3. **Property Delegation for Grammars**:
   - Support idiomatic property delegates (`val rule by def { ... }`) that automatically derive production names from `KProperty.name` without reflection, while still resolving into an optimized, inlined parser graph.
4. **DSLs & Type-Safe Builders**:
   - Provide builder functions with trailing lambdas: `buildExpression<T> { ... }`.
5. **High-Performance Memory Patterns**:
   - Bitset lookups (`LookupCharPredicate` using `IntArray`) for O(1) character checks.
   - String repeaters (`starString()`, `plusString()`) that slice the underlying `buffer` or use `StringBuilder` without allocating intermediate `List<Char>`.
   - Lazy Kotlin `Sequence` for streaming match results (`matches(input)`).

---

## 3. Directory & Parallel Test Hierarchy

The codebase strictly adheres to a **parallel test hierarchy**. Every source file in `commonMain` must have an exact 1:1 counterpart in `commonTest`.

```text
src/
├── commonMain/kotlin/org/petitparser/
│   ├── core/
│   │   ├── context/
│   │   │   ├── Input.kt
│   │   │   ├── Output.kt
│   │   │   ├── ParseError.kt
│   │   │   └── Token.kt
│   │   ├── definition/
│   │   │   ├── GrammarDefinition.kt
│   │   │   └── Resolve.kt
│   │   ├── expression/
│   │   │   ├── Builder.kt
│   │   │   ├── Group.kt
│   │   │   └── Result.kt
│   │   ├── parser/
│   │   │   ├── Parser.kt
│   │   │   ├── action/
│   │   │   ├── combinator/
│   │   │   ├── consumer/
│   │   │   ├── misc/
│   │   │   └── repeater/
│   │   ├── reflection/
│   │   │   ├── Analyzer.kt
│   │   │   ├── Iterable.kt
│   │   │   ├── Linter.kt
│   │   │   ├── Optimize.kt
│   │   │   └── Transform.kt
│   │   ├── debug/
│   │   ├── indent/
│   │   └── matcher/
│   └── grammars/
└── commonTest/kotlin/org/petitparser/
    ├── core/
    │   ├── context/
    │   ├── definition/
    │   ├── expression/
    │   ├── parser/
    │   ├── reflection/
    │   ├── debug/
    │   ├── indent/
    │   └── matcher/
    └── grammars/
```

### Unit Test Completeness Rule

- **Every line of code and branch must be covered by a unit test.**
- If a method adds parameters with default values, write tests asserting both the default behavior and custom overrides.
- For every parser, create test pairs:
  - **Success cases**: Exact parsed values, positions, and nested outputs.
  - **Failure cases**: Exact failure message and failure position.
  - **Edge cases**: Empty input `""`, single-character boundary, unexpected input, unconsumed trailing tokens.

---

## 4. Testing Harness & Invariant Assertions

Tests must use the standard PetitParser assertion utilities defined in `org.petitparser.core.parser.Assertions.kt`:

### 4.1 `assertSuccess`

Verifies that:

1. `parser.accept(input)` returns `true`.
2. `parser.parse(input)` produces `Output.Success` with the expected `value` and `position`.
3. Reading `output.message` throws `UnsupportedOperationException`.
4. `parser.fastParseOn(input, 0)` returns the exact same success position as `output.position`.

```kotlin
assertSuccess(parser, "123", 123)
assertSuccess(parser, "123a", 123, position = 3)
```

### 4.2 `assertFailure`

Verifies that:

1. `parser.accept(input)` returns `false`.
2. `parser.parse(input)` produces `Output.Failure` with the expected `message` and `position`.
3. Reading `output.value` throws `ParseError`.
4. `parser.fastParseOn(input, 0)` returns `-1`.

```kotlin
assertFailure(parser, "abc", message = "digit expected", position = 0)
```

### 4.3 Parser Invariants Checklist

Whenever implementing a new parser, verify:

- **Copying**: `parser.copy()` produces an identical, independent node with equal children.
- **Equality**: `parser.isEqualTo(parser.copy())` is `true`.
- **String representation**: `parser.toString()` includes class name, configuration, or label.
- **Child substitution**: For composite parsers, `parser.replace(child, replacement)` correctly mutates the referenced child.

---

## 5. How to Run Tests

### JVM Tests (Fastest Iteration)

Run all JVM unit tests:

```bash
./gradlew jvmTest
```

Run a specific test class:

```bash
./gradlew jvmTest --tests "org.petitparser.core.expression.ExpressionTest"
```

Run a single test method:

```bash
./gradlew jvmTest --tests "org.petitparser.core.expression.ExpressionTest.test_addition"
```

### JS Tests (Node.js & Headless Browser)

Run all JavaScript engine tests:

```bash
./gradlew jsTest
```

### Full Multiplatform Verification

Run the entire check suite across all configured targets:

```bash
./gradlew check
```

---

## 6. Coding Style & Conventions

- **Indentation**: Exactly 2 spaces. No tabs.
- **Naming**:
  - Combinator functions: lowercase verbs/nouns (`seq`, `or`, `map`, `repeat`, `flatten`, `trim`).
  - Concrete parser classes: PascalCase with `Parser` suffix (`SequenceParser`, `MapParser`, `ChoiceParser`).
  - Predicate classes: PascalCase with `CharPredicate` suffix (`LookupCharPredicate`, `RangeCharPredicate`).
- **Token Efficiency**:
  - Keep responses minimal and technical.
  - Do not restate unchanged code.
  - Do not introduce unnecessary dependencies or third-party libraries.
- **Immutability vs Mutability**:
  - Parsers are immutable by default after assembly.
  - Child references inside `DelegateParser` (`delegate`) and `ListParser` (`parsers`) are mutable purely to support recursive graph resolution (`resolve()`, `replace()`, and `ExpressionBuilder`).
- **Null Safety**:
  - Never use double assertion `!!` unless guaranteed by prior condition.
  - Distinguish explicitly between successful parse yielding `null` (`Parser<R?>`) and failure (`Output.Failure`).
- **Anti-Loop Protocol**:
  - If a test or build fails, create the smallest reproducible delta.
  - Limit fix iterations to 2 attempts per failure before halting to inspect divergences.
