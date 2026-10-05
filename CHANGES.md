# 1.2.0

- Modernized `GrammarDefinition` DSL with property delegation (`val rule by def { ... }`) and typed function references (`ref(::rule)`).
- Full `filter()` overload parity with custom messages and message callbacks as first-class idiomatic Kotlin.
- High-performance allocation-free `fastParseOn` throughout the combinator hierarchy and fast `accept()`.
- Added `ExpressionBuilder` DSL with support for prefix, postfix, left- and right-associative infix operators.
- Added stateful indentation parsing via `Indent.during(...)` with automatic failure rollback.
- Added diagnostic parity on `Input` and `Output`: `toPositionString()`, `line`, `column`, and factory `Input(buffer, position)`.
- Optimized `Token`: lazy computed `input` and `length`, and cached newline parser.
- Deprecated legacy `Grammar` in favor of `GrammarDefinition`.
- Deprecated `seq2`..`seq9` in favor of typed `seq(...)`.
- Deprecated `ref0`..`ref9` in favor of `ref(...)`.
- Deprecated `success()` in favor of `epsilon()`.
- Modernized `CsvGrammar` and `JsonGrammar` to extend `GrammarDefinition` and use `.surroundedBy(...)`.
- Added AST transformation, optimizer (`optimize()`), and linter (`linter()`) infrastructure.
- Added parallel test hierarchy with comprehensive multiplatform coverage (JVM, JS, and Native).

# 1.1.0

- Renamed the untyped sequence list constructor from `seq()` to `seqOf()`, and make `seq()` return typed tuples.
- Add `Char.toParser()` and `String.toParser()`.
- Add missing `Parser.neg()` operator.
- Fix and update build confirmation.

# 1.0.0

- Initial release of PetitParser for Kotlin.