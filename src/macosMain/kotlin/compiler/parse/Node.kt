package compiler.parse

interface Node

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
