package org.petitparser.core.grammar

import org.petitparser.core.context.Input
import org.petitparser.core.context.Output
import org.petitparser.core.parser.Parser
import kotlin.reflect.KProperty

abstract class Grammar {
  private val parsers = mutableMapOf<String, Parser<*>>()

  /** Forward reference to a parser defined later. */
  protected fun <R> ref(provider: () -> Parser<R>) = object : Parser<R> {
    private val delegate: Parser<R> by lazy(provider)
    override fun parseOn(input: Input): Output<R> = delegate.parseOn(input)
  }

  protected operator fun <R> Parser<R>.provideDelegate(
    thisRef: Grammar,
    property: KProperty<*>,
  ): Parser<R> = also { parsers[property.name] = it }

  protected operator fun <R> Parser<R>.getValue(
    thisRef: Grammar,
    property: KProperty<*>,
  ): Parser<R> = this
}