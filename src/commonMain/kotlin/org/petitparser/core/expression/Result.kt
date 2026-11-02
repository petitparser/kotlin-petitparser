package org.petitparser.core.expression

/** Encapsulates a prefix operation. */
class ExpressionResultPrefix<V, O>(
  val operator: O,
  val callback: (O, V) -> V,
) {
  operator fun invoke(value: V): V = callback(operator, value)

  override fun toString(): String = "ExpressionResultPrefix($operator)"
}

/** Encapsulates a postfix operation. */
class ExpressionResultPostfix<V, O>(
  val operator: O,
  val callback: (V, O) -> V,
) {
  operator fun invoke(value: V): V = callback(value, operator)

  override fun toString(): String = "ExpressionResultPostfix($operator)"
}

/** Encapsulates an infix operation. */
class ExpressionResultInfix<V, O>(
  val operator: O,
  val callback: (V, O, V) -> V,
) {
  operator fun invoke(left: V, right: V): V = callback(left, operator, right)

  override fun toString(): String = "ExpressionResultInfix($operator)"
}
