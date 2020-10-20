package compiler.parse

import compiler.AST
import compiler.exceptions.ParserException
import compiler.parse.ExpressionNode.NumberNode
import compiler.parse.TerminationNode.EOLNode
import compiler.tokenize.*
import compiler.utils.log

fun List<Token>.parse(): AST = filterNot { it.type is Whitespace }
    .toProvider()
    .run { mutableListOf<ProgramNode>().also { while (hasNext()) it.add(parseStatement()) } }

private fun TokenProvider.parseStatement(): ProgramNode = when (currentToken.type) {
    is Keyword -> parseKeyword()
    else -> throw ParserException("Statement does not start with keyword or identifier: ${currentToken.type}")
}

private fun TokenProvider.parseKeyword(): ProgramNode {
    log("Parsing keyword: ${currentToken.value}")
    return when (currentToken.type) {
        is Print -> parsePrintStatement()
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
    is EndOfLine -> EOLNode
    else -> throw ParserException("Token of type: $type is not an End Of Line token")
}.also { eatToken() }
