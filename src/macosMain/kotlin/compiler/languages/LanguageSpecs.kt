package compiler.languages

import compiler.tokenize.*

interface LanguageSpec {
    val matchers: List<Pair<Regex, Token.Type>>
}

object DasmLanguageSpec : LanguageSpec {

    override val matchers = listOf(
        Regex("^waarde ") to Value,
        Regex("^druk af") to Print,
        Regex("^wordt") to Assignment,
        Regex("^-?[.0-9]+") to Number,
        Regex("^\\s+") to Whitespace,
        Regex("^[a-zA-Z]+") to Identifier,
        Regex("^;") to EndOfLine
    )
}

object SwasmLanguageSpec : LanguageSpec {

    override val matchers = listOf(
        Regex("^print MOFO!1!!") to Print,
        Regex("^-?[.0-9]+") to Number,
        Regex("^\\s+") to Whitespace,
        Regex("^[a-zA-Z]+") to Identifier,
        Regex("^;") to EndOfLine
    )

}