# PetitParser Kotlin Parity Tasks

Actionable task list to bring Kotlin PetitParser (`src`) to complete feature parity with canonical Dart PetitParser (`/Users/renggli/Programming/Dart/PetitParser`), designed natively for Kotlin with full backward compatibility and modern best practices.

---

## Phase 1: Core Engine Architecture & AST Foundation

- [ ] **1.1 Backward-Compatible `fun interface Parser<out R>` Enhancement**
  - Keep `Parser<out R>` as a Kotlin `fun interface` with single abstract method `fun parseOn(input: Input): Output<R>`, preserving 100% backward compatibility for SAM conversions and lambda parsers `Parser { input -> ... }`.
  - Add interface default method: `fun fastParseOn(buffer: String, position: Int): Int`. Default implementation delegates to `parseOn`.
  - Add interface default method: `fun copy(): Parser<R> = this`.
  - Add interface default method: `val children: List<Parser<*>> get() = emptyList()`.
  - Add interface default method: `fun replace(source: Parser<*>, target: Parser<*>) {}`.
  - Add interface default method: `fun isEqualTo(other: Any?, seen: MutableSet<Parser<*>> = mutableSetOf()): Boolean`.
  - Add interface default hooks: `hasEqualProperties(other: Parser<*>)` and `hasEqualChildren(other: Parser<*>, seen: MutableSet<Parser<*>>)`.
  - Reference: `lib/src/core/parser.dart`.

- [ ] **1.2 Combinator Base Class Hierarchy**
  - Implement `abstract class DelegateParser<T, out R>(var delegate: Parser<T>) : Parser<R>` for single-child AST delegation, replacement, and traversal.
  - Implement `abstract class ListParser<R, out S>(children: List<Parser<R>>) : Parser<S>` for multi-child AST delegation, replacement, and traversal.
  - Reference: `lib/src/parser/combinator/delegate.dart`, `lib/src/parser/combinator/list.dart`.

- [ ] **1.3 Test Harness Upgrades**
  - Update `assertSuccess` in `Assertions.kt` to assert that `fastParseOn` returns the expected end position without allocating `Output`.
  - Update `assertFailure` in `Assertions.kt` to assert that `fastParseOn` returns `-1`.
  - Add `expectParserInvariants` helper to assert `copy()`, `isEqualTo()`, `children`, `replace()`, and `toString()`.
  - Reference: `test/utils/assertions.dart`, `test/utils/matchers.dart`.

---

## Phase 2: Actions, Combinators & Repeaters (Kotlin-Native & Fast)

- [ ] **2.1 Action Parsers Concrete Migration & Additions**
  - Convert `map` to `MapParser<T, R>` (inherits `DelegateParser`, delegates `fastParseOn` directly without running the transformation callback).
  - Convert `pick` to `PickParser<R>` (inherits `DelegateParser`, delegates `fastParseOn`).
  - Convert `flatten` to `FlattenParser<R>` (inherits `DelegateParser`, uses `fastParseOn` when `message != null`).
  - Convert `token` to `TokenParser<R>` (inherits `DelegateParser`).
  - Convert `trim` to `TrimmingParser<R>` (inherits `DelegateParser`, holds `left` and `right`, implements allocation-free `fastParseOn` loop).
  - Convert `callCC` to `ContinuationParser<T, R>` (inherits `DelegateParser`).
  - Convert `filter` to `FilterParser<T>` (inherits `DelegateParser`).
  - Add `constant(value)` / `ConstantParser<T, R>` returning a static value on success.
  - Add `permute(vararg indices)` / `PermuteParser<R>` reordering list elements.
  - Add `castList<R>()` / `CastListParser<T, R>` casting elements of a parsed list.
  - Add `where(predicate, message, factory)` with custom failure message formatting.
  - Reference: `lib/src/parser/action/`.

- [ ] **2.2 Combinator Parsers Concrete Migration & Operator Overloads**
  - Convert `or` / `div` to `ChoiceParser<R>` (inherits `ListParser`, implements short-circuiting `fastParseOn`). Leverage `Parser<out R>` covariance so choices of subtypes automatically infer their common supertype (e.g., `sealed interface AstNode`).
  - Convert `seqOf` / `seq` to `SequenceParser<R>` (inherits `ListParser`, implements sequential `fastParseOn`).
  - Add Kotlin operator overloads: `operator fun <R> Parser<R>.plus(other: Parser<Any?>): Parser<List<Any?>>` for sequence concatenation.
  - Add delimiter-stripping combinators to eliminate dummy tuple mappings:
    - `followedBy(delimiter)` / `skip(after)`: parses receiver then delimiter, returning receiver's typed value.
    - `precededBy(delimiter)` / `skip(before)`: parses delimiter then receiver, returning receiver's typed value.
    - `surroundedBy(left, right)`: parses left, receiver, right, returning receiver's typed value.
  - Convert `and` to `AndParser<R>` (inherits `DelegateParser`, zero-consumption `fastParseOn`).
  - Convert `not` to `NotParser<R>` (inherits `DelegateParser`, inverted `fastParseOn`).
  - Convert `optional` to `OptionalParser<R>` with strict nullability preservation:
    - `fun <R> Parser<R>.optional(): Parser<R?>` returning nullable value.
    - `fun <R : Any> Parser<R>.optional(otherwise: R): Parser<R>` returning non-nullable value with fallback.
  - Convert `settable` to `SettableParser<R>` (inherits `DelegateParser`).
  - Convert `SequenceTuple.kt` to concrete `SequenceParser2`..`SequenceParser9` with direct field access, child replacement, and chained `fastParseOn`.
  - Provide direct N-ary mapping via `seqMap(p1, p2, ...) { a, b -> ... }` for zero intermediate tuple allocations, alongside `seq(p1, p2).map { (a, b) -> ... }` destructuring.
  - Reference: `lib/src/parser/combinator/`.

- [ ] **2.3 Repeater Parsers Concrete Migration & String Specializations**
  - Convert `repeat` / `star` / `plus` / `times` to `PossessiveRepeatingParser<R>` (inherits `DelegateParser`, implements loop `fastParseOn`).
  - Convert `starGreedy` / `plusGreedy` to `GreedyRepeatingParser<R>` (inherits `DelegateParser`, holds `limit`, implements backtrack `fastParseOn`).
  - Convert `starLazy` / `plusLazy` to `LazyRepeatingParser<R>` (inherits `DelegateParser`, holds `limit`, implements lazy `fastParseOn`).
  - Convert `Separated.kt` to `SeparatedParser<R, S>` (inherits `DelegateParser`, holds `separator`, implements interleaved `fastParseOn`).
  - Add `foldLeft` and `foldRight` methods to `SeparatedList<R, S>`.
  - Implement `RepeatingCharacterParser` directly returning `String` using buffer slicing without allocating intermediate `List<Char>`.
  - Add `starString()`, `plusString()`, `timesString()`, `repeatString()` extensions.
  - Reference: `lib/src/parser/repeater/`, `lib/src/parser/utils/separated_list.dart`.

- [ ] **2.4 Misc Parsers**
  - Convert `endOfInput` to `EndOfInputParser` with `fastParseOn`.
  - Convert `failure` to `FailureParser<R>` with `fastParseOn`.
  - Convert `position` to `PositionParser` with `fastParseOn`.
  - Convert `success` to `EpsilonParser<R>` with `fastParseOn`.
  - Add `epsilon()` and `epsilonWith(value)` aliases.
  - Add `label(name)` and `LabeledParser<R>` for AST annotations and debug traces.
  - Reference: `lib/src/parser/misc/`.

---

## Phase 3: Character Parsers, Predicates & Optimization

- [ ] **3.1 Character Predicates & Fast Bitset Lookup**
  - Fix bug in `CharPredicate.none()` where `test(char)` was returning `true` instead of `false`.
  - Implement `LookupCharPredicate` using bitset tables (`IntArray`) for O(1) character dispatch on compact ranges (<= 1 KB).
  - Implement character predicate range optimizer in `CharPredicate.ranges` that merges adjacent/overlapping ranges and dynamically switches between single-char, range, `LookupCharPredicate`, or binary search on `starts`/`stops`.
  - Add built-in predicates for `word`, `letter`, `digit`, `lowercase`, `uppercase`.
  - Reference: `lib/src/parser/character/predicate/`, `lib/src/parser/character/utils/optimize.dart`.

- [ ] **3.2 Additional Character Parsers**
  - Add `lowercase(message)` parser.
  - Add `uppercase(message)` parser.
  - Add `word(message)` parser accepting `[a-zA-Z0-9_]`.
  - Reference: `lib/src/parser/character/lowercase.dart`, `uppercase.dart`, `word.dart`.

- [ ] **3.3 Consumer Class Conversions & Unicode**
  - Convert `char` to `CharParser` (inherits `Parser<Char>`, implements `fastParseOn`).
  - Convert `newline` to `NewlineParser` (inherits `Parser<String>`, handles `\n`, `\r\n`, `\r`, implements `fastParseOn`).
  - Convert `string` to `StringParser` (inherits `Parser<String>`, supports `ignoreCase`, implements `fastParseOn`).
  - Implement `UnicodeCharacterParser` for UTF-16 surrogate pair parsing beyond BMP.
  - Reference: `lib/src/parser/predicate/single_character.dart`, `lib/src/parser/predicate/unicode_character.dart`, `lib/src/parser/predicate/string.dart`.

---

## Phase 4: Grammar Definition & Expression Builder (Kotlin Idioms)

- [ ] **4.1 Expression Builder Subsystem & DSL**
  - Create package `org.petitparser.core.expression`.
  - Implement `ExpressionResultPrefix<V, O>`, `ExpressionResultPostfix<V, O>`, and `ExpressionResultInfix<V, O>`.
  - Implement `ExpressionGroup<T>` supporting `prefix`, `postfix`, `left` (left-associative binary), `right` (right-associative binary), `wrapper` (brackets/parentheses), and `optional`.
  - Implement `ExpressionBuilder<T>` coordinating primitives, groups, loopback substitution, and compilation.
  - Add Kotlin DSL entrypoint: `inline fun <T> buildExpression(builderAction: ExpressionBuilder<T>.() -> Unit): Parser<T>`.
  - Add unit tests verifying arithmetic precedence, associativity, parentheses, prefix/postfix negation/increments.
  - Reference: `lib/src/expression/`.

- [ ] **4.2 Grammar Definition & Resolution**
  - Create package `org.petitparser.core.definition`.
  - Implement `ResolvableParser` interface for parsers that delegate to another parser during resolution.
  - Support idiomatic Kotlin grammar building:
    1. Property delegation (`val rule by def { ... }`) that derives rule names automatically from `KProperty.name` without reflection.
    2. Typed forward references via callable references: `ref(::rule)` where `::rule` is a `KProperty0<Parser<R>>`, inferring `Parser<R>` with zero boilerplate (eliminating Dart's clumsy `ref0`..`ref9`).
    3. Parameterized productions using standard Kotlin methods: `fun token(p: Parser<String>) = p.trim()`.
  - Implement `resolve<R>(root: Parser<R>): Parser<R>` to traverse the graph, inline `ResolvableParser` references, and eliminate indirection wrappers.
  - Implement `GrammarDefinition<R>` base class with `start()`, `build()`, and `buildFrom(production)`.
  - Add unit tests for recursive and parameterized grammar definitions.
  - Reference: `lib/src/definition/`.

---

## Phase 5: Reflection, Analyzer, Optimizer & Linter

- [ ] **5.1 Graph Traversal & Structural Transformation**
  - Create package `org.petitparser.core.reflection`.
  - Implement `allParsers(root: Parser<*>): Sequence<Parser<*>>` depth-first traversal returning lazy Kotlin `Sequence` of unique reachable parsers.
  - Implement `allChildren(root: Parser<*>): Sequence<Parser<*>>` lazy sequence of direct/indirect children.
  - Implement `transformParser<R>(parser: Parser<R>, handler: (Parser<*>) -> Parser<*>): Parser<R>` for deep copying and AST tree transformation.
  - Reference: `lib/src/reflection/iterable.dart`, `lib/src/reflection/transform.dart`.

- [ ] **5.2 Grammar Analyzer**
  - Implement `Analyzer` computing:
    - Nullability analysis (`isNullable`).
    - First-set computation (`firstSet`).
    - Follow-set computation (`followSet`).
    - Cycle-set detection (`cycleSet`).
  - Reference: `lib/src/reflection/analyzer.dart`, `lib/src/reflection/internal/`.

- [ ] **5.3 Parser Optimizer**
  - Implement `optimize<R>(parser: Parser<R>): Parser<R>`.
  - Implement rule `FlattenChoice`: merges nested choices with the same failure joiner.
  - Implement rule `RemoveDelegate`: removes redundant `SettableParser` and `LabeledParser` wrappers.
  - Implement rule `RemoveDuplicate`: unifies structurally identical parsers.
  - Implement rule `CharacterRepeater`: transforms `flatten(repeat(character))` into `RepeatingCharacterParser`.
  - Reference: `lib/src/reflection/optimize.dart`, `lib/src/reflection/internal/optimize_rules.dart`.

- [ ] **5.4 Grammar Linter**
  - Implement `linter(parser: Parser<*>): List<LinterIssue>`.
  - Port all 13 Dart linter rules:
    - `CharacterRepeater`
    - `DuplicateParser`
    - `LeftRecursion`
    - `NestedChoice`
    - `NullableRepeater`
    - `OverlappingChoice`
    - `RepeatedChoice`
    - `UnnecessaryFlatten`
    - `UnnecessaryResolvable`
    - `UnoptimizedFlatten`
    - `UnreachableChoice`
    - `UnresolvedSettable`
    - `UnusedResult`
  - Reference: `lib/src/reflection/linter.dart`, `lib/src/reflection/internal/linter_rules.dart`.

---

## Phase 6: Debugging, Indentation & Extended Matchers

- [ ] **6.1 Debugging Tools**
  - Create package `org.petitparser.core.debug`.
  - Implement `trace<R>(parser: Parser<R>, output: (TraceEvent) -> Unit = ::println): Parser<R>`.
  - Implement `profile<R>(parser: Parser<R>, output: (ProfileResult) -> Unit = ::println): Parser<R>`.
  - Implement `progress<R>(parser: Parser<R>, callback: (Input) -> Unit): Parser<R>`.
  - Reference: `lib/src/debug/`.

- [ ] **6.2 Indentation Combinator**
  - Create package `org.petitparser.core.indent`.
  - Implement `Indent` parser supporting indentation-sensitive languages (Python/YAML style):
    - `same`: matches current indentation level.
    - `during`: scoped block matching with automatic stack push/pop and state rollback on failure.
  - Reference: `lib/src/indent/`.

- [ ] **6.3 Extended Matchers**
  - Create package `org.petitparser.core.matcher`.
  - Implement `accept(input: CharSequence, start: Int = 0): Boolean`.
  - Implement `matches(input: CharSequence, overlapping: Boolean = false, start: Int = 0): Sequence<R>`.
  - Implement `matchesSkipping(input: CharSequence, start: Int = 0): Sequence<R>`.
  - Implement `ParserPattern` adapter for string search.
  - Reference: `lib/src/matcher/`.

---

## Phase 7: Verification & Documentation

- [ ] **7.1 Full Test Porting**
  - Port `test/expression_test.dart` -> `src/commonTest/.../ExpressionTest.kt`.
  - Port `test/definition_test.dart` -> `src/commonTest/.../DefinitionTest.kt`.
  - Port `test/reflection_test.dart` -> `src/commonTest/.../ReflectionTest.kt`.
  - Port `test/debug_test.dart` -> `src/commonTest/.../DebugTest.kt`.
  - Port `test/indent_test.dart` -> `src/commonTest/.../IndentTest.kt`.
  - Port `test/matcher_test.dart` -> `src/commonTest/.../MatcherTest.kt`.
- [ ] **7.2 Multiplatform Target Verification**
  - Run `./gradlew jvmTest`.
  - Run `./gradlew jsTest`.
  - Verify clean builds on Native targets (`macosArm64`, `linuxX64`, etc.).
