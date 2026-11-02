package org.petitparser.core.definition

import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.misc.LabeledParser
import org.petitparser.core.parser.misc.label
import kotlin.reflect.KProperty

/**
 * Helper to conveniently define and build complex, recursive grammars using plain Kotlin code.
 */
abstract class GrammarDefinition<R> {
  private val productions = mutableMapOf<String, Parser<*>>()

  /**
   * Registers a production with the given [name] and [parser].
   */
  fun registerProduction(name: String, parser: Parser<*>) {
    productions[name] = parser
  }

  /**
   * Returns the production with the given [name], or `null` if not found.
   */
  operator fun get(name: String): Parser<*>? =
    productions[name] ?: if (name == "start") try { start() } catch (_: UnsupportedOperationException) { null } else null

  /**
   * All registered production names.
   */
  val productionNames: Set<String> get() = productions.keys

  /**
   * Returns the starting production of this definition.
   * By default, returns the production registered with the name "start".
   */
  @Suppress("UNCHECKED_CAST")
  open fun start(): Parser<R> {
    val startProduction = productions["start"]
    if (startProduction != null) {
      return startProduction as Parser<R>
    }
    throw UnsupportedOperationException("Missing start production in ${this::class.simpleName}")
  }

  /**
   * Builds the default composite parser starting at [start].
   */
  open fun build(): Parser<R> = buildFrom(ref(::start))

  /**
   * Builds a composite parser starting with the specified [parser].
   */
  fun <T> buildFrom(parser: Parser<T>): Parser<T> = resolve(parser)

  /**
   * Builds a composite parser starting from the production with the specified [name].
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> buildFrom(name: String): Parser<T> {
    val production = get(name)
      ?: throw IllegalArgumentException("Unknown production '$name' in ${this::class.simpleName}")
    return buildFrom(production as Parser<T>)
  }

  /**
   * Defines a production parser with delayed evaluation, automatically capturing
   * the property name as the production name.
   */
  protected fun <T> def(definition: () -> Parser<T>): ProductionDelegate<T> =
    org.petitparser.core.definition.def(definition)

  /**
   * Alias for [def].
   */
  protected fun <T> rule(definition: () -> Parser<T>): ProductionDelegate<T> =
    def(definition)

  /**
   * Enables `val production by parser` syntax inside [GrammarDefinition].
   */
  @Suppress("UNCHECKED_CAST")
  protected operator fun <T> Parser<T>.provideDelegate(
    thisRef: GrammarDefinition<*>,
    property: KProperty<*>,
  ): Parser<T> {
    val labeled: Parser<T> = if (this is LabeledParser<*>) this as Parser<T> else this.label(property.name)
    registerProduction(property.name, labeled)
    return labeled
  }

  protected operator fun <T> Parser<T>.getValue(
    thisRef: GrammarDefinition<*>,
    property: KProperty<*>,
  ): Parser<T> = this
}
