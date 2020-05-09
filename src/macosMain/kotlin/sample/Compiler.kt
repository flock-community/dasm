package sample

fun String.compile() = tokenize().parse().emit()

typealias StatementNode = Token
typealias Program = List<StatementNode>
