package org.petitparser.core.definition

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.misc.LabeledParser
import org.petitparser.core.parser.misc.label
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property delegate that captures the production [name] from `KProperty.name`
 * and delays evaluation of the parser definition until resolved.
 */
class ProductionDelegate<T>(
  private val definition: () -> Parser<T>,
) : ReadOnlyProperty<Any?, Parser<T>>, ResolvableParser<T> {
  var name: String? = null
    private set
  var owner: Any? = null
    private set
  private var resolvedParser: Parser<T>? = null

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ProductionDelegate<T> {
    name = property.name
    owner = thisRef
    if (thisRef is GrammarDefinition<*>) {
      thisRef.registerProduction(property.name, this)
    }
    return this
  }

  override fun getValue(thisRef: Any?, property: KProperty<*>): Parser<T> = this

  override fun resolve(): Parser<T> {
    var parser = resolvedParser
    if (parser == null) {
      parser = definition()
      val n = name
      if (n != null && parser !is LabeledParser<*>) {
        parser = parser.label(n)
      }
      resolvedParser = parser
    }
    return parser
  }

  override fun parseOn(input: Input): Output<T> = resolve().parseOn(input)

  override fun fastParseOn(buffer: String, position: Int): Int = resolve().fastParseOn(buffer, position)

  override fun copy(): ProductionDelegate<T> = this

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProductionDelegate<*>) return false
    return owner !== null && owner === other.owner && name != null && name == other.name
  }

  override fun hashCode(): Int =
    if (owner != null && name != null) 31 * owner.hashCode() + name.hashCode()
    else super.hashCode()

  override fun hasEqualProperties(other: Parser<*>): Boolean = this == other

  override fun toString(): String = name?.let { "ProductionDelegate($it)" } ?: "ProductionDelegate"
}

/**
 * Defines a production parser with delayed evaluation.
 */
fun <T> def(definition: () -> Parser<T>): ProductionDelegate<T> = ProductionDelegate(definition)

/**
 * Alias for [def].
 */
fun <T> rule(definition: () -> Parser<T>): ProductionDelegate<T> = def(definition)
