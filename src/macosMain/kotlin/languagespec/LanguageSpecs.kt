package languagespec

import sample.*

interface LanguageSpec {
    val matchers: List<Pair<Regex, Token.Type>>
}

object DasmLanguageSpec : LanguageSpec {

    override val matchers = listOf(
                Regex("^waarde ") to ValueKeyword,
                Regex("^druk af") to PrintKeyword,
                Regex("^wordt") to Assignment,
                Regex("^-?[.0-9]+") to Number,
                Regex("^\\s+") to Whitespace,
                Regex("^[a-zA-Z]+") to Identifier,
                Regex("^;") to EOL
            )
}

object SwasmLanguageSpec: LanguageSpec {

    override val matchers = listOf(
        Regex("^print MOFO!1!!") to PrintKeyword,
        Regex("^-?[.0-9]+") to Number,
        Regex("^\\s+") to Whitespace,
        Regex("^[a-zA-Z]+") to Identifier,
        Regex("^;") to EOL
    )

}