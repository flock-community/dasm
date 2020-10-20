package sample

import languagespec.LanguageSpec
import sample.exceptions.TokenizerException

interface Keyword : Token.Type
object PrintKeyword: Keyword
object ValueKeyword: Keyword

object Assignment : Token.Type
object Number : Token.Type
object Whitespace : Token.Type
object Identifier : Token.Type
object EOL : Token.Type
object EOP : Token.Type


infix fun LanguageSpec.tokenize(source: String) = source.tokenize(this) + Token(type = EOP, value = "EOP", index = source.length + 1L)

private fun String.tokenize(languageSpec: LanguageSpec, index: Long = 1L): List<Token> =
    when (val token = findToken(index, languageSpec)) {
        null -> throw noTokenFoundException(index)
        else -> with(removePrefix(token.value)) {
            if (isEmpty()) listOf(token) else listOf(token) + tokenize(languageSpec, index + token.value.length)
        }
    }

private fun String.findToken(index: Long, languageSpec: LanguageSpec): Token? {
    return languageSpec.matchers
        .findPossibleMatches(this)
        .filterOnlyMatched()
        .mapToTokens(index)
        .firstOrNull()
}

private fun String.noTokenFoundException(index: Long) = TokenizerException("Op positie $index in de code is '${first()}' in wat hier volgt '${substring(0..25)}...' niet wat we verwachten")

fun List<Pair<Regex, Token.Type>>.findPossibleMatches(string: String) = map { (regex, tokenType) -> regex.find(string)?.value to tokenType }
fun List<Pair<String?, Token.Type>>.filterOnlyMatched() = mapNotNull { (matched, tokenType) -> matched?.run { this to tokenType } }
fun List<Pair<String, Token.Type>>.mapToTokens(index: Long) = map { (matched, tokenType) -> Token(tokenType, matched, index) }

data class Token(
    val type: Type,
    val value: String,
    val index: Long
) {
    interface Type
}