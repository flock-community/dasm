package sample

import sample.exceptions.ParserException

class TokenProvider(private val tokenIterator: Iterator<Token>) {

    var currentToken = nextToken() ?: throw ParserException("CurrentToken cannot be null")
    var nextToken = nextToken()

    init {
        printTokens()
    }

    fun eatToken() {
        currentToken = nextToken ?: throw ParserException("NextToken cannot be null")
        nextToken = nextToken()

        printTokens()
    }

    private fun printTokens() =
        println("currentToken ${currentToken.value} nextToken: ${nextToken?.value}")

    fun hasNext() = nextToken != null

    private fun nextToken() = kotlin.runCatching { tokenIterator.next() }.getOrNull()
}

fun List<Token>.parse(): Program {

    val filteredList = this.filterNot { it.type is Whitespace }
    val tokenProvider = TokenProvider(filteredList.iterator())
    val nodes = mutableListOf<ProgramNode>()
    while (tokenProvider.hasNext()) {
        nodes.add(tokenProvider.parseStatement())
    }
    return nodes
}

private fun TokenProvider.parseStatement(): ProgramNode = when (currentToken.type) {
    is Keyword -> parseKeyword()
    else -> throw ParserException("Statement does not start with keyword or identifier")
}

private fun TokenProvider.parseKeyword(): ProgramNode = when (currentToken.value) {
    "druk af" -> parsePrintStatement()
    "waarde" -> parseVariableDeclarationStatement()
    else -> throw ParserException("Onbekend keyword")
}.also {
    println("Parsing keyword: ${this.currentToken.value}")
}


private fun TokenProvider.parsePrintStatement(): ProgramNode {
    println("Parsing print statement: ${this.currentToken}")
    eatToken()
    return ProgramNode.PrintStatement(parseExpression())
}

private fun TokenProvider.parseExpression(): ExpressionNode = when (currentToken.type) {
    is Identifier -> ExpressionNode.IdentifierNode(currentToken.value)
    is Number -> ExpressionNode.NumberNode(currentToken.value.toLong())
    else -> throw ParserException("Onbekend token type: ${currentToken.type}")
}

fun TokenProvider.parseVariableDeclarationStatement(): ProgramNode {
    println("Parsing variable declaration: ${this.currentToken}")
    eatToken()
    val variable = currentToken.value
    if (nextToken?.value != "wordt") throw ParserException("Assignment verwacht")
    eatToken()
    return ProgramNode.VariableDeclaration(variable, parseExpression())
}

sealed class ProgramNode() {
    class PrintStatement(val expression: ExpressionNode) : ProgramNode()
    class VariableDeclaration(val name: String, val initializer: ExpressionNode) : ProgramNode()
}

sealed class ExpressionNode {
    class IdentifierNode(val value: String) : ExpressionNode()
    class NumberNode(val number: Long) : ExpressionNode()
}

