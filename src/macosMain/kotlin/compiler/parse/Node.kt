package compiler.parse

import compiler.parse.ExpressionNode.IdentifierNode
import compiler.parse.ExpressionNode.NumberNode

interface Node

sealed class ProgramNode : Node {

    data class Print(val expression: ExpressionNode) : ProgramNode()
    data class VariableAndAssignmentDeclaration(
        val identifier: IdentifierNode,
        val expression: ExpressionNode
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
