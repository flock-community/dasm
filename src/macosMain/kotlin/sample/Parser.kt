package sample

import sample.ExpressionNode.NumberNode
import sample.TerminationNode.EOLNode
import sample.exceptions.ParserException
import sample.utils.log

class TokenProvider(private val tokenIterator: Iterator<Token>) {

    var currentToken = nextToken() ?: throw ParserException("CurrentToken cannot be null")
    var nextToken = nextToken()

    init {
        printTokens()
    }

    fun eatToken() {
        val previousToken = currentToken
        currentToken = nextToken ?: throw ParserException("NextToken cannot be null")
        nextToken = nextToken()

        printTokens(previousToken.value)
    }

    private fun printTokens(previous: String? = null) =
        log("\n${if (previous == null) "" else "Eating: $previous, "}Current: '${currentToken.value}' Next: '${nextToken?.value}'\n")

    fun hasNext() = nextToken != null

    private fun nextToken() = kotlin.runCatching { tokenIterator.next() }.getOrNull()
}

fun List<Token>.parse(): AST {

    val filteredList = filterNot { it.type is Whitespace }
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

private fun TokenProvider.parseKeyword(): ProgramNode {
    log("Parsing keyword: ${currentToken.value}")
    return when (currentToken.value) {
        "druk af" -> parsePrintStatement()
//    "waarde" -> parseVariableDeclarationAndAssignmentStatement()
        else -> throw ParserException("Unknown keyword")
    }
}

private fun TokenProvider.parsePrintStatement(): ProgramNode {
    log("Parsing print statement")
    eatToken()
    val expression = parseExpression()
    parseEndOfLine()
    return ProgramNode.PrintStatement(expression)
}

private fun TokenProvider.parseExpression(): ExpressionNode {
    log("Parsing Expression starting with: $currentToken")
    return when (currentToken.type) {
//    is Identifier -> IdentifierNode(currentToken.value)
        is Number -> NumberNode(currentToken.value.toFloat())
//    is Assignment -> AssignmentNode(currentToken.value)
        else -> throw ParserException("Unknown token type: ${currentToken.type}")
    }.also { eatToken() }
}

private fun TokenProvider.parseEndOfLine(): TerminationNode = when (val type = currentToken.type) {
    is EOL -> EOLNode
    else -> throw ParserException("Token of type: $type is not an End Of Line token")
}.also { eatToken() }

fun TokenProvider.parseVariableDeclarationAndAssignmentStatement(): ProgramNode {
    log("Parsing variable declaration: ${this.currentToken}")
    eatToken()
    val variable = currentToken.value // getal
    if (nextToken?.value != "wordt") throw ParserException("Assignment expected, got: '${nextToken?.value}'")
    eatToken()
    eatToken()
    val numberExpression = parseExpression()
    eatToken()
    if (currentToken.type != EOL) throw ParserException("; expected")
    return ProgramNode.VariableAndAssignmentDeclaration(variable, parseExpression(), numberExpression).also {
        log("Parsed variable declaration: $it")
    }
}




sealed class ProgramNode : Node {

    data class PrintStatement(val expression: ExpressionNode) : ProgramNode()
    data class VariableAndAssignmentDeclaration(
        val name: String,
        val initializer: ExpressionNode,
        val numberNode: ExpressionNode
    ) : ProgramNode() // Temp NumberNode
}

sealed class ExpressionNode : Node {
    data class IdentifierNode(val value: String) : ExpressionNode()
    data class NumberNode(val number: Float) : ExpressionNode()
    data class AssignmentNode(val value: String) : ExpressionNode()
}

sealed class TerminationNode : Node {
    object EOLNode : TerminationNode()
    object EOPNode : TerminationNode()
}

interface Node