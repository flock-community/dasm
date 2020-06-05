package sample

fun String.compile() = tokenize().parse().emit()

typealias Program = List<ProgramNode>
