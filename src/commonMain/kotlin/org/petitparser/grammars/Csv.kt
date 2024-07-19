package org.petitparser.grammars

import org.petitparser.core.grammar.Grammar
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.seqMap
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.starSeparated
import org.petitparser.core.parser.repeater.starString

class CsvGrammar : Grammar() {
  private val fieldContent by pattern("^,\n\r").starString()
  private val quotedFieldContent by (string("\"\"").map { _ -> '"' } / pattern("^\"")).star()
    .map { chars -> chars.joinToString("") }

  private val quotedField by seqMap(char('"'), quotedFieldContent, char('"')) { _, value, _ -> value }
  private val field by quotedField / fieldContent

  private val record by field.starSeparated(char(',')).map { list -> list.elements }
  private val lines by record.starSeparated(newline()).map { list -> list.elements }

  val start by lines.end()
}
