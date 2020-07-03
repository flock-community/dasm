package sample

import sample.ExpressionNode.*
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
        println("currentToken: '${currentToken.value}' nextToken: '${nextToken?.value}'")

    fun hasNext() = nextToken != null

    private fun nextToken() = kotlin.runCatching { tokenIterator.next() }.getOrNull()
}

fun List<Token>.parse(): List<StatementNode> {

    val filteredList = this.filterNot { it.type is Whitespace }
    val tokenProvider = TokenProvider(filteredList.iterator())
    val nodes = mutableListOf<StatementNode>()
    while (tokenProvider.hasNext()) {
        nodes.add(tokenProvider.parseStatement())
    }
    return nodes
}

private fun TokenProvider.parseStatement(): StatementNode = when (currentToken.type) {
    is Keyword -> parseKeyword()
    else -> throw ParserException("Statement does not start with keyword or identifier: ${currentToken.type}")
}

private fun TokenProvider.parseKeyword(): StatementNode = when (currentToken.value) {
    "druk af" -> parsePrintStatement()
    "waarde" -> parseVariableDeclarationAndAssignmentStatement()
    else -> throw ParserException("Unknown keyword")
}.also {
    println("Parsing keyword: ${this.currentToken.value}")
}

private fun TokenProvider.parsePrintStatement(): StatementNode {
    println("Parsing print statement: ${this.currentToken}")
    eatToken()
    return StatementNode.PrintStatement(parseExpression())
}

private fun TokenProvider.parseExpression(): ExpressionNode = when (currentToken.type) {
    is Identifier -> IdentifierNode(currentToken.value)
    is Number -> NumberNode(currentToken.value.toLong())
    is Assignment -> AssignmentNode(currentToken.value)
    is EOL -> EOLNode
    else -> throw ParserException("Unknown token type: ${currentToken.type}")
}

fun TokenProvider.parseVariableDeclarationAndAssignmentStatement(): StatementNode {
    println("Parsing variable declaration: ${this.currentToken}")
    eatToken()
    val variable = currentToken.value // getal
    if (nextToken?.value != "wordt") throw ParserException("Assignment expected, got: '${nextToken?.value}'")
    eatToken()
    eatToken()
    val numberExpression = parseExpression()
    eatToken()
    if(currentToken.type != EOL) throw ParserException("; expected")
    return StatementNode.VariableAndAssignmentDeclaration(variable, parseExpression(), numberExpression).also {
        println("Parsed variable declaration: $it")
    }
}

typealias Program = List<StatementNode>

sealed class StatementNode {
    data class PrintStatement(val expression: ExpressionNode) : StatementNode()
    data class VariableAndAssignmentDeclaration(val name: String, val initializer: ExpressionNode, val numberNode: ExpressionNode) : StatementNode() // Temp NumberNode
}

sealed class ExpressionNode {
    data class IdentifierNode(val value: String) : ExpressionNode()
    data class NumberNode(val number: Long) : ExpressionNode()
    data class AssignmentNode(val value: String) : ExpressionNode()
    object EOLNode : ExpressionNode()
}

