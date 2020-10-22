package compiler.parse

import compiler.parse.ExpressionNode.IdentifierNode
import compiler.parse.ExpressionNode.NumberNode

interface Node

sealed class ProgramNode : Node {

    data class PrintStatement(val expression: ExpressionNode) : ProgramNode()
    data class VariableAndAssignmentDeclaration(
        val identifierNode: IdentifierNode,
        val numberNode: NumberNode
    ) : ProgramNode() // Temp NumberNode
}

sealed class ExpressionNode : Node {
    data class IdentifierNode(val value: String, val set: Boolean = false) : ExpressionNode() {
        fun set() = copy(set = true)
        fun get() = copy(set = false)
    }

    data class NumberNode(val number: Float) : ExpressionNode()
    data class AssignmentNode(val value: String) : ExpressionNode()
}

sealed class TerminationNode : Node {
    object EOLNode : TerminationNode()
    object EOPNode : TerminationNode()
}
