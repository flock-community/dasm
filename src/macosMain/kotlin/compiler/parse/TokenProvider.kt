package compiler.parse

import compiler.exceptions.ParserException
import compiler.tokenize.Token
import compiler.utils.log

class TokenProvider(private val tokenIterator: Iterator<Token>) {

    var token = nextToken() ?: throw ParserException("CurrentToken cannot be null")
    var nextToken = nextToken()

    init {
        printTokens()
    }

    fun hasNext() = nextToken != null

    fun eatToken() {
        val previousToken = token
        token = nextToken ?: throw ParserException("NextToken cannot be null")
        nextToken = nextToken()

        printTokens(previousToken.value)
    }

    private fun printTokens(previous: String? = null) =
        log("\n${if (previous == null) "" else "Eating: $previous, "}Current: '${token.value}' Next: '${nextToken?.value}'\n")


    private fun nextToken() = kotlin.runCatching { tokenIterator.next() }.getOrNull()
}

fun List<Token>.toProvider() = TokenProvider(iterator())
