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
    else -> throw ParserException("Statement does not start with keyword or identifier: ${currentToken.type}")
}

private fun TokenProvider.parseKeyword(): ProgramNode = when (currentToken.value) {
    "druk af" -> parsePrintStatement()
    "waarde" -> parseVariableDeclarationAndAssignmentStatement()
    else -> throw ParserException("Unknown keyword")
}.also {
    println("Parsing keyword: ${this.currentToken.value}")
}

private fun TokenProvider.parsePrintStatement(): ProgramNode {
    println("Parsing print statement: ${this.currentToken}")
    eatToken()
    return ProgramNode.PrintStatement(parseExpression())
}

private fun TokenProvider.parseExpression(): ExpressionNode = when (currentToken.type) {
    is Identifier -> IdentifierNode(currentToken.value)
    is Number -> NumberNode(currentToken.value.toLong())
    is Assignment -> AssignmentNode(currentToken.value)
    is EOL -> EOLNode
    else -> throw ParserException("Unknown token type: ${currentToken.type}")
}

fun TokenProvider.parseVariableDeclarationAndAssignmentStatement(): ProgramNode {
    println("Parsing variable declaration: ${this.currentToken}")
    eatToken()
    val variable = currentToken.value // getal
    if (nextToken?.value != "wordt") throw ParserException("Assignment expected, got: '${nextToken?.value}'")
    eatToken()
    eatToken()
    val numberExpression = parseExpression()
    eatToken()
    if(currentToken.type != EOL) throw ParserException("; expected")
    return ProgramNode.VariableAndAssignmentDeclaration(variable, parseExpression(), numberExpression).also {
        println("Parsed variable declaration: $it")
    }
}

sealed class ProgramNode {
    data class PrintStatement(val expression: ExpressionNode) : ProgramNode()
    data class VariableAndAssignmentDeclaration(val name: String, val initializer: ExpressionNode, val numberNode: ExpressionNode) : ProgramNode() // Temp NumberNode
}

sealed class ExpressionNode {
    data class IdentifierNode(val value: String) : ExpressionNode()
    data class NumberNode(val number: Long) : ExpressionNode()
    data class AssignmentNode(val value: String) : ExpressionNode()
    object EOLNode : ExpressionNode()
}