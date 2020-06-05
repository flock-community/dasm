package sample

import sample.exceptions.ParserException

class TokenProvider(private val tokenIterator: Iterator<Token>) {

    var currentToken = nextToken() ?: throw ParserException("CurrentToken cannot be null")
    var nextToken = nextToken()

    fun eatToken() {
        currentToken = nextToken ?: throw ParserException("NextToken cannnot be null")
        nextToken = nextToken()
    }

    fun hasNext() = nextToken != null

    private fun nextToken() = kotlin.runCatching { tokenIterator.next() }.getOrNull()
}





typealias ProvideTokens = () -> Pair<Token?, Token?>
typealias EatToken = () -> Unit

fun List<Token>.parse(): Program {


    val tokenProvider = TokenProvider(iterator())

    var nodes = mutableListOf<ProgramNode>()

    while (tokenProvider.hasNext()) {

        nodes.add(parseStatement(tokenProvider))
    }

    return nodes
}

private fun parseStatement(tokenProvider: TokenProvider): ProgramNode {

    return when (tokenProvider.currentToken.type) {
        is Keyword -> parseKeyword(tokenProvider)
        else -> throw ParserException("Statement does not start with keyword or identifier")
    }
}

private fun parseKeyword(tokenProvider: TokenProvider): ProgramNode = when (tokenProvider.currentToken.value) {
    "druk af" -> parsePrintStatement(tokenProvider)
    "waarde" -> parseVariableDeclarationStatement(tokenProvider)
    else -> throw ParserException("Onbekend keyword")
}


private fun parsePrintStatement(tokenProvider: TokenProvider): ProgramNode {

    tokenProvider.eatToken()

    val expressionNode = parseExpression(tokenProvider)

    return ProgramNode.PrintStatement(expressionNode)
}

private fun parseExpression(tokenProvider: TokenProvider): ExpressionNode {
    val currentToken = tokenProvider.currentToken
    return when (currentToken.type) {
        is Identifier -> ExpressionNode.IdentifierNode(currentToken.value)
        is Number -> ExpressionNode.NumberNode(currentToken.value.toLong())
        else -> throw ParserException("Onbekend token type: ${currentToken.type}")
    }
}

fun parseVariableDeclarationStatement(tokenProvider: TokenProvider): ProgramNode {

    // currentToken = waarde
    tokenProvider.eatToken()
    val variable = tokenProvider.currentToken.value
    // currentToken = getal
    // nextToken = wordt
    if (tokenProvider.nextToken?.value != "wordt") throw ParserException("Assignment verwacht")
    tokenProvider.eatToken()
    // currentToken 14

    return ProgramNode.VariableDeclaration(variable, parseExpression(tokenProvider))
}

sealed class ProgramNode() {

    class PrintStatement(val expression: ExpressionNode) : ProgramNode()
    class VariableDeclaration(val name: String, val initializer: ExpressionNode) : ProgramNode()
}

sealed class ExpressionNode {

    class IdentifierNode(val value: String) : ExpressionNode()
    class NumberNode(val number: Long) : ExpressionNode()

}

