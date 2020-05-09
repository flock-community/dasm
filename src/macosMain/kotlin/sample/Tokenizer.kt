package sample

object Keyword : Token.Type
object Assignment : Token.Type
object Number : Token.Type
object Whitespace : Token.Type
object Identifier : Token.Type

val keywords = listOf(
    "waarde",
    "druk af"
)

val matchers = listOf(
    Regex("^(${keywords.joinToString("|")})") to Keyword,
    Regex("^wordt") to Assignment,
    Regex("^-?[.0-9]+") to Number,
    Regex("^\\s+") to Whitespace,
    Regex("^[a-zA-Z]+") to Identifier
)

fun String.tokenize(): List<Token> = matchers
    .map { it.first.find(this)?.value to it.second }
    .mapNotNull { it.first?.run { this to it.second } }
    .map { Token(it.second, it.first, 1) }
    .firstOrNull()
    ?.let { with(removePrefix(it.value)) { if (isEmpty()) listOf(it) else listOf(it) + tokenize() } }
    ?: throw RuntimeException("'${this.first()}' is niet gedefinieerd")

data class Token(
    val type: Type,
    val value: String,
    val index: Long
) {
    interface Type
}
