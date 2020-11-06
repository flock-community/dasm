package compiler.parse

import compiler.AST
import compiler.exceptions.ParserException
import compiler.parse.ExpressionNode.*
import compiler.parse.ProgramNode.PrintStatement
import compiler.parse.ProgramNode.VariableAndAssignmentDeclaration
import compiler.tokenize.*
import compiler.utils.log

fun List<Token>.parse(): AST = filterNot { it.type is Whitespace }
    .toProvider()
    .run { parser() }

private fun TokenProvider.parser(): AST = mutableListOf<ProgramNode>()
    .also { while (hasNext()) it.add(parseStatement()) }

private fun TokenProvider.parseStatement(): ProgramNode = token
    .also { log("Parsing Statement with token: '${it.type}'") }
    .run {
        when (type) {
            is Keyword -> parseKeyword()
            else -> throw ParserException("Statement does not start with keyword or identifier: '$type'")
        }
    }

private fun TokenProvider.parseKeyword(): ProgramNode = token
    .also { log("Parsing keyword: '${it.value}'") }
    .run {
        when (type) {
            is Print -> parsePrintStatement()
            is Value -> parseVariableDeclarationAndAssignmentStatement()
            else -> throw ParserException("Unknown keyword: '$value'")
        }
    }

private fun TokenProvider.parsePrintStatement(): PrintStatement = token
    .also { log("Parsing Print statement with Token: ${it.type}") }
    .let {
        eatToken()
        val expression = parseExpression()
        parseEndOfLine()
        PrintStatement(expression)
    }

private fun TokenProvider.parseVariableDeclarationAndAssignmentStatement(): VariableAndAssignmentDeclaration = token
    .also { log("Parsing Variable Declaration And Assignment statement with Token: ${it.type}") }
    .let {
        eatToken()
        val identifierNode = parseExpression() as IdentifierNode
        parseExpression() as AssignmentNode
        val numberNode = parseExpression() as NumberNode
        parseEndOfLine()
        VariableAndAssignmentDeclaration(identifierNode, numberNode)
    }

private fun TokenProvider.parseExpression(): ExpressionNode = token
    .also { log("Parsing Expression starting with Token: ${it.type}") }
    .run {
        when (type) {
            is Identifier -> IdentifierNode(value)
            is Number -> NumberNode(value.toFloat())
            is Assignment -> AssignmentNode(value)
            else -> throw ParserException("Unknown Token of type: '$type'")
        }
    }
    .also { eatToken() }

private fun TokenProvider.parseEndOfLine() = token
    .also { log("Parsing End Of Line with Token: ${it.type}") }
    .run {
        when (type) {
            is EndOfLine -> eatToken()
            else -> throw ParserException("Token of type: '$type' is not an End Of Line token")
        }
    }
