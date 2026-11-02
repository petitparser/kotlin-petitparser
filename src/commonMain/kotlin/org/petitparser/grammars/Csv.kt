package org.petitparser.grammars

import org.petitparser.core.definition.GrammarDefinition
import org.petitparser.core.definition.ref
import org.petitparser.core.parser.Parser
import org.petitparser.core.parser.action.map
import org.petitparser.core.parser.combinator.div
import org.petitparser.core.parser.combinator.surroundedBy
import org.petitparser.core.parser.consumer.char
import org.petitparser.core.parser.consumer.newline
import org.petitparser.core.parser.consumer.pattern
import org.petitparser.core.parser.consumer.string
import org.petitparser.core.parser.misc.end
import org.petitparser.core.parser.repeater.star
import org.petitparser.core.parser.repeater.starSeparated
import org.petitparser.core.parser.repeater.starString

class CsvGrammar : GrammarDefinition<List<List<String>>>() {
  val fieldContent: Parser<String> by def { pattern("^,\n\r").starString() }
  val quotedFieldContent: Parser<String> by def {
    (string("\"\"").map { '"' } / pattern("^\"")).star()
      .map { chars -> chars.joinToString("") }
  }

  val quotedField: Parser<String> by def {
    ref(::quotedFieldContent).surroundedBy(char('"'))
  }
  val field: Parser<String> by def { ref(::quotedField) / ref(::fieldContent) }

  val record: Parser<List<String>> by def {
    ref(::field).starSeparated(char(',')).map { list -> list.elements }
  }
  val lines: Parser<List<List<String>>> by def {
    ref(::record).starSeparated(newline()).map { list -> list.elements }
  }

  val start: Parser<List<List<String>>> by def { ref(::lines).end() }
}
