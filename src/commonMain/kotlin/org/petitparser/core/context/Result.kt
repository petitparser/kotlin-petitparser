package org.petitparser.core.context

interface Result<out R> : Context {
  val value: R
  val message: String
}

