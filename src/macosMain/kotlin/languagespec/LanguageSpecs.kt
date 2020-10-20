package languagespec

import sample.*

interface LanguageSpec {
    val keywords:List<String>
    val matchers: List<Pair<Regex, Token.Type>>
}

object DasmLanguageSpec : LanguageSpec {

    override val keywords: List<String> = listOf(
        "waarde",
        "druk af"
    )
    override val matchers = listOf(
        Regex("^(${keywords.joinToString("|")})") to Keyword,
        Regex("^wordt") to Assignment,
        Regex("^-?[.0-9]+") to Number,
        Regex("^\\s+") to Whitespace,
        Regex("^[a-zA-Z]+") to Identifier,
        Regex("^;") to EOL
    )
}

object KotlinLanguageSpec: LanguageSpec {

    override val keywords: List<String> = listOf(
        "println"
    )
    override val matchers = listOf(
        Regex("^(${keywords.joinToString("|")})") to Keyword,
        Regex("^-?[.0-9]+") to Number,
        Regex("^\\s+") to Whitespace,
        Regex("^[a-zA-Z]+") to Identifier,
        Regex("^;") to EOL
    )

}