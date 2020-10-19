package sample

import sample.exceptions.TokenizerException

object Keyword : Token.Type
object Assignment : Token.Type
object Number : Token.Type
object Whitespace : Token.Type
object Identifier : Token.Type
object EOL : Token.Type
object EOP : Token.Type

val keywords = listOf(
    "waarde",
    "druk af"
)

val matchers = listOf(
    Regex("^(${keywords.joinToString("|")})") to Keyword,
    Regex("^wordt") to Assignment,
    Regex("^-?[.0-9]+") to Number,
    Regex("^\\s+") to Whitespace,
    Regex("^[a-zA-Z]+") to Identifier,
    Regex("^;") to EOL
)

fun String.tokenize(index: Long = 1L): List<Token> = matchers
    .map { it.first.find(this)?.value to it.second }
    .mapNotNull { it.first?.run { this to it.second } }
    .map { Token(it.second, it.first, index) }
    .firstOrNull()
    ?.let { with(removePrefix(it.value)) { if (isEmpty()) listOf(it) else listOf(it) + tokenize(index + it.value.length) } }
    ?: throw TokenizerException("Op positie $index in de code is '${first()}' in wat hier volgt '${substring(0..25)}...' niet wat we verwachten")

data class Token(
    val type: Type,
    val value: String,
    val index: Long
) {
    interface Type
}
